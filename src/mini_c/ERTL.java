package mini_c;

/** Explicit Register Transfer Language (ERTL) */

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.max;
import static mini_c.Mbinop.Mmov;
import static mini_c.Register.*;

/** instruction ERTL */

/** les mêmes que dans RTL */

abstract class ERTL {
  abstract void accept(ERTLVisitor v);
  abstract Label[] succ();

  abstract Set<Register> def();
  abstract Set<Register> use();

  protected static Set<Register> emptySet = new HashSet<>();
  protected static Set<Register> singleton(Register r) { Set<Register> s = new HashSet<>(); s.add(r); return s; }
  protected static Set<Register> pair(Register r1, Register r2) { Set<Register> s = singleton(r1); s.add(r2); return s; }

  abstract Register getR();
  Register getConflict() { return null; }

  abstract void toLTL(LTLgraph lg, Coloring coloring, Label key, int n, int m);
}

class ERconst extends ERTL {
  public int i;
  public Register r;
  public Label l;
  ERconst(int i, Register r, Label l) { this.i = i; this.r = r; this.l = l; }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "mov $" + i + " " + r + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return singleton(r); }
  @Override Set<Register> use() { return emptySet; }
  @Override Register getR() { return r; }
  @Override
  void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Lconst lconst = new Lconst(this.i, coloring.get(r), l);
    lg.put(key, lconst);
  }
}

class ERaccess_global extends ERTL {
  public String s;
  public Register r;
  public Label l;
  ERaccess_global(String s, Register r, Label l) { this.s = s; this.r = r; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + s + " " + r + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return singleton(r); }
  @Override Set<Register> use() { return emptySet; }
  @Override Register getR() { return r; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Operand o = coloring.get(this.r);
    if ( o instanceof Reg) {  // color(r) is a physic register
      Laccess_global laccess_global = new Laccess_global(this.s, ((Reg) o).r, this.l);
      lg.put(key, laccess_global);
    }
    else { // color(r) is in the pile
      /* L2 : mov %rbp n(%rsp) */
      Lmbinop lmbinop = new Lmbinop(Mmov, new Reg(tmp1), o, this.l);
      Label L2 = lg.add(lmbinop);
      /* L1 : mov x %rsp */
      Laccess_global laccess_global = new Laccess_global(this.s, tmp1, L2);
      lg.put(key, laccess_global);
    }
  }
}

class ERassign_global extends ERTL {
  public Register r;
  public String s;
  public Label l;
  ERassign_global(Register r, String s, Label l) { this.r = r; this.s = s; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + r + " " + s + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return singleton(r); }
  @Override Register getR() { return null; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Operand o = coloring.get(this.r);
    if ( o instanceof Reg) {  // color(r) is a physic register
      Lassign_global lassign_global = new Lassign_global(((Reg) o).r, this.s, this.l);
      lg.put(key, lassign_global);
    }
    else { // color(r) is in the pile
      /* L2 : mov %rbp n */
      Lassign_global lassign_global = new Lassign_global(tmp1, this.s, this.l);
      Label L2 = lg.add(lassign_global);
      /* L1 : mov n(%rsp) %rbp */
      Lmbinop lmbinop = new Lmbinop( Mmov, o, new Reg(tmp1), L2);
      lg.put(key, lmbinop);
    }
  }
}

class ERload extends ERTL {
  public Register r1;
  public int i;
  public Register r2;
  public Label l;
  ERload(Register r1, int i, Register r2, Label l) { this.r1 = r1;
    this.i = i; this.r2 = r2; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + i + "(" + r1 + ") " + r2 + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return singleton(r2); }
  @Override Set<Register> use() { return singleton(r1); }
  @Override Register getR() { return r2; }
  @Override Register getConflict() { return r1; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Operand o1 = coloring.get(this.r1);
    Operand o2 = coloring.get(this.r2);
    if ( (o1 instanceof Reg) && (o2 instanceof Reg) ) { // Ideal case <3 two registers
      Lstore lstore = new Lstore(((Reg) o1).r, ((Reg) o2).r, this.i, this.l);
      lg.put(key, lstore);
    }
    else if ( (o1 instanceof Spilled) && (o2 instanceof Spilled)) { // Worst case -.- two pill
      Lstore lstore = new Lstore(tmp1, tmp2, this.i, this.l);
      Label L3 = lg.add(lstore);
      Lmbinop lmbinop = new Lmbinop(Mmov, o2, new Reg(tmp2), L3);
      Label L2 = lg.add(lmbinop);
      lmbinop = new Lmbinop(Mmov, o1, new Reg(tmp1), L2);
      lg.put(key, lmbinop);
    }
    else {  // only one is on the pile
      if ( o1 instanceof Spilled ) {
        /* L2 : store rbp n(r2) */
        Lstore lstore = new Lstore(tmp1, ((Reg)o2).r, this.i, this.l);
        Label L2 = lg.add(lstore);
        /* L1 : mov n(rsp) rbp */
        Lmbinop lmbinop = new Lmbinop(Mmov, o1, new Reg(tmp1), L2);
        lg.put(key, lmbinop);
      }
      else if ( o2 instanceof Spilled) { // not necessary the else if but I think it's more clear
        /* L2 : store r1 n(r11) */
        Lstore lstore = new Lstore( ((Reg)o1).r, tmp2, this.i, this.l);
        Label L2 = lg.add(lstore);
        /* L1 : mov n(rsp) r11 */
        Lmbinop lmbinop = new Lmbinop(Mmov, o2, new Reg(tmp2), L2);
        lg.put(key, lmbinop);
      }
    }
  }
}

class ERstore extends ERTL {
  public Register r1;
  public Register r2;
  public int i;
  public Label l;
  ERstore(Register r1, Register r2, int i, Label l) { this.r1 = r1;
    this.r2 = r2; this.i = i; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + r1 + " " + i + "(" + r2 + ") " + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return pair(r1, r2); }
  @Override Register getR() { return r2; }
  @Override Register getConflict() { return r1; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Operand o1 = coloring.get(this.r1);
    Operand o2 = coloring.get(this.r2);
    if ( (o1 instanceof Reg) && (o2 instanceof Reg) ) { // Ideal case <3 two registers
      Lload lload = new Lload(((Reg) o1).r, this.i, ((Reg) o2).r, this.l);
      lg.put(key, lload);
    }
    else if ( (o1 instanceof Spilled) && (o2 instanceof Spilled) ) { // Worst case -.- two pill
      Lload lload = new Lload(tmp1, this.i, tmp2, this.l);
      Label L3 = lg.add(lload);
      Lmbinop lmbinop = new Lmbinop(Mmov, o2, new Reg(tmp2), L3);
      Label L2 = lg.add(lmbinop);
      lmbinop = new Lmbinop(Mmov, o1, new Reg(tmp1), L2);
      lg.put(key, lmbinop);
    }
    else {  // only one is on the pile
      if ( o1 instanceof Spilled ) {
        /* L2 : store rbp n(r2) */
        Lload lload = new Lload(tmp1, this.i, ((Reg)o2).r, this.l);
        Label L2 = lg.add(lload);
        /* L1 : mov n(rsp) rbp */
        Lmbinop lmbinop = new Lmbinop(Mmov, o1, new Reg(tmp1), L2);
        lg.put(key, lmbinop);
      }
      else if ( o2 instanceof Spilled) { // not necessary the else if but I think it's more clear
        /* L2 : store r1 n(r11) */
        Lload lload = new Lload( ((Reg)o1).r, this.i, tmp2, this.l);
        Label L2 = lg.add(lload);
        /* L1 : mov n(rsp) r11 */
        Lmbinop lmbinop = new Lmbinop(Mmov, o2, new Reg(tmp2), L2);
        lg.put(key, lmbinop);
      }
    }
  }
}

class ERmunop extends ERTL {
  public Munop m;
  public Register r;
  public Label l;
  ERmunop(Munop m, Register r, Label l) { this.m = m; this.r = r; this.l = l; }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return singleton(r); }
  @Override Set<Register> use() { return singleton(r); }
  @Override Register getR() { return r; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Lmunop lmunop = new Lmunop(this.m, coloring.get(this.r), this.l);
    lg.put(key, lmunop);
  }
}

class ERmbinop extends ERTL {
  public Mbinop m;
  public Register r1;
  public Register r2;
  public Label l;
  ERmbinop(Mbinop m, Register r1, Register r2, Label l) { this.m = m; this.r1 = r1; this.r2 = r2; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r1 + " " + r2 + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { 
    if (m == Mbinop.Mdiv) {
      assert (r2.equals(Register.rax));
      return pair(Register.rax, Register.rdx);
    }
    else { return singleton(r2); }
  }
  @Override Set<Register> use() {
    if (m == Mbinop.Mdiv) { return pair(Register.rax, r1); }
    else if (m == Mmov) { return singleton(r1); }
    else { return pair(r1, r2); }
  }
  @Override Register getR() { return r2; }
  @Override Register getConflict() { return r1; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Operand o1 = coloring.get(r1);
    Operand o2 = coloring.get(r2);
    switch (this.m) {
      case Mmov:
        if (o1.equals(o2)) { // mov r r
          Lgoto lgoto = new Lgoto(this.l);
          lg.put(key, lgoto);
          return;
        }
      case Mmul: // x86-64 imul need the second argument to be a physical register
        if (o2 instanceof Spilled) {
          /* L2 : imul r1 rbp -> L */
          Lmbinop lmbinop = new Lmbinop(this.m, o1, new Reg(tmp1), this.l);
          Label L2 = lg.add(lmbinop);
          /* L1 : mov r2 rbp -> L2 */
          lmbinop = new Lmbinop(Mmov, o2, new Reg(tmp1), L2);
          lg.put(key, lmbinop);
          return;
        }
      default:
        if (o1 instanceof Spilled && o2 instanceof Spilled) { // binary operations can't have both registers on memory!
          /* L2 : imul r1 rbp -> L */
          Lmbinop lmbinop = new Lmbinop(this.m, o1, new Reg(tmp1), this.l);
          Label L2 = lg.add(lmbinop);
          /* L1 : mov r2 rbp -> L2 */
          lmbinop = new Lmbinop(Mmov, o2, new Reg(tmp1), L2);
          lg.put(key, lmbinop);
        } else {  // Rest of the cases.
          Lmbinop lmbinop = new Lmbinop(this.m, o1, o2, this.l);
          lg.put(key, lmbinop);
        }
    }
  }
}

class ERmubranch extends ERTL {
  public Mubranch m;
  public Register r;
  public Label l1;
  public Label l2;
  ERmubranch(Mubranch m, Register r, Label l1, Label l2) { this.m = m; this.r = r; this.l1 = l1; this.l2 = l2;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r + " --> " + l1 + ", " + l2; }
  Label[] succ() { return new Label[] { l1, l2 }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return singleton(r); }
  @Override Register getR() { return r; }

  @Override
  void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    // TODO
  }
}

class ERmbbranch extends ERTL {
  public Mbbranch m;
  public Register r1;
  public Register r2;
  public Label l1;
  public Label l2;
  ERmbbranch(Mbbranch m, Register r1, Register r2, Label l1, Label l2) {
    this.m = m; this.r1 = r1; this.r2 = r2; this.l1 = l1; this.l2 = l2;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r1 + " " + r2 + " --> " + l1 + ", " + l2; }
  Label[] succ() { return new Label[] { l1, l2 }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return pair(r1, r2); }
  @Override Register getR() { return r2; }
  @Override Register getConflict() { return r1; }

  @Override
  void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    // TODO
  }
}

class ERgoto extends ERTL {
  public Label l;
  ERgoto(Label l) { this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "goto " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return emptySet; }
  @Override Register getR() { return null; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Lgoto lgoto = new Lgoto(this.l);
    lg.put(key, lgoto);
  }
}

/** modifiée */
// TODO: LTLcall
class ERcall extends ERTL {
  public String s;
  public int i;
  public Label l;
  ERcall(String s, int i, Label l) { this.s = s; this.i = i; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "call " + s + "(" + i + ") --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return new HashSet<>(Register.caller_save); }
  @Override Set<Register> use() {
    Set<Register> s = new HashSet<>();
    int k = i;
    assert (k <= Register.parameters.size());
    for (Register r: Register.parameters) {
      if (k-- == 0) break;
      s.add(r);
    }
    return s;
  }
  @Override Register getR() { return null; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {

  }
}

/** nouvelles instructions */

class ERalloc_frame extends ERTL {
  public Label l;
  ERalloc_frame(Label l) { this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "alloc_frame --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return emptySet; }
  @Override Register getR() { return null; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int n, int m) {
    // TODO: check if m is 0 and make a goto (maybe this can be general to many functions so to do it on Maddi and not in here)
    Lmunop lmunop = new Lmunop(new Maddi(-8*m), new Reg(rsp), this.l);
    lg.put(key, lmunop);
  }
}

class ERdelete_frame extends ERTL {
  public Label l;
  ERdelete_frame(Label l) { this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "delete_frame --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return emptySet; }
  @Override Register getR() { return null; }

  @Override
  void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    // TODO: check if m is 0 and make a goto (maybe this can be general to many functions so to do it on Maddi and not in here)
    Lmunop lmunop = new Lmunop(new Maddi(8*m), new Reg(rsp), this.l);
    lg.put(key, lmunop);
  }
}

class ERget_param extends ERTL {
  public int i;
  public Register r;
  public Label l;
  ERget_param(int i, Register r, Label l) { this.i = i; this.r = r; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "mov stack(" + i + ") --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return singleton(r); }
  @Override Set<Register> use() { return emptySet; }
  @Override Register getR() { return r; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int n, int m) {
    int s = 8 * (1 + max(0, n - parameters.size()) + m);
    Spilled o1 = new Spilled(this.i + s);
    Operand o2 = coloring.get(this.r);
    if ( o2 instanceof Reg ) {
      Lmbinop lmbinop = new Lmbinop(Mmov, o1, o2, this.l);
      lg.put(key, lmbinop);
    }
    else if (o2 instanceof Spilled) {  // The if is unnecessary but I think is more clear
      /* L2 : Mmov rbp n(rsp) -> L */
      Lmbinop lmbinop = new Lmbinop(Mmov, new Reg(tmp1), o2, this.l);
      Label L2 = lg.add(lmbinop);
      /* L1 : Mmov k(rsp) rbp -> L2 */
      lmbinop = new Lmbinop(Mmov, o1, new Reg(tmp1), L2);
      lg.put(key, lmbinop);
    }
  }
}

class ERpush_param extends ERTL {
  public Register r;
  public Label l;
  ERpush_param(Register r, Label l) { this.r = r; this.l = l;  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "push " + r + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() { return singleton(r); }
  @Override Register getR() { return r; }
  @Override void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Lpush_param lpush_param = new Lpush_param(coloring.get(this.r), this.l);
    lg.put(key, lpush_param);
  }
}

class ERreturn extends ERTL {
  ERreturn() {  }
  void accept(ERTLVisitor v) { v.visit(this); }
  public String toString() { return "return"; }
  Label[] succ() { return new Label[] { }; }
  @Override Set<Register> def() { return emptySet; }
  @Override Set<Register> use() {
    Set<Register> s = new HashSet<>(Register.callee_saved);
    s.add(Register.rax);
    return s;
  }
  @Override Register getR() { return null; }
  @Override
  void toLTL(LTLgraph lg, Coloring coloring, Label key, int formals, int m) {
    Lreturn lreturn = new Lreturn();
    lg.put(key, lreturn);
  }
}

/** une fonction ERTL */

class ERTLfun {
  /** nom de la fonction */
  public String name;
  /** nombre total d'arguments */
  public int formals;
  /** ensemble des variables locales */
  public Set<Register> locals;
  /** point d'entrée dans le graphe */
  public Label entry;
  /** le graphe de flot de contrôle */
  public ERTLgraph body;
  /** Duration of life variable */
  public Liveness info;
  /** Interference */
  private Interference interference;
  /** Color map */
  public Coloring coloring;
  
  ERTLfun(String name, int formals) { this.name = name; this.formals = formals; this.locals = new HashSet<>(); }
  void accept(ERTLVisitor v) { v.visit(this); }

  /** pour débugger */
  void print() {
    System.out.println("== ERTL =========================");
    System.out.println(name + "(" + formals + ")");
    System.out.println("  entry  : " + entry);
    System.out.println("  locals : " + locals);
    body.printWithLife(new HashSet<>(), this.entry, this.info);
    if (interference != null) { interference.print(); }
    if (coloring != null ) { coloring.print(); }
  }

  void createLiveness() { info = new Liveness(this.body); }
  void createInterference() {
    if (info == null) { throw new Error("to create interference, info must be created"); }
    interference = new Interference(info);
  }
  void createColormap() {
    if ( interference == null) { throw new Error("To create colormap, the interference must first be done"); }
    coloring = new Coloring(interference);
  }
}

class ERTLfile {
  public LinkedList<String> gvars;
  public LinkedList<ERTLfun> funs;
  ERTLfile() {
    this.gvars = new LinkedList<>();
    this.funs = new LinkedList<>();
  }
  void accept(ERTLVisitor v) { v.visit(this); }

  /** pour débugger */
  void print() {
    for (ERTLfun fun: this.funs)
      fun.print();
  }
}

/** graphe de flot de contrôle (d'une fonction)
 * 
 * c'est un dictionnaire qui associe une instruction de type RTL
 * à une étiquette de type Label
 */
class ERTLgraph {
  Map<Label, ERTL> graph = new HashMap<Label, ERTL>();

  /** ajoute une nouvelle instruction dans le graphe
    * et renvoie son étiquette */
  Label add(ERTL instr) {
    Label l = new Label();
    graph.put(l, instr);
    return l;
  }
  
  void put(Label l, ERTL instr) {
    graph.put(l, instr);
  }
  
  // imprime le graphe par un parcours en profondeur
  private void print(Set<Label> visited, Label l) {
    if (visited.contains(l)) return;
    visited.add(l);
    ERTL r = this.graph.get(l);
    if (r == null) return; // c'est le cas pour exit
    System.out.println("  " + String.format("%3s", l) + ": " + r );
    for (Label s: r.succ()) print(visited, s);
  }
  void printWithLife(Set<Label> visited, Label l, Liveness info) {
    if (visited.contains(l)) return;
    visited.add(l);
    ERTL r = this.graph.get(l);
    if (r == null) return; // c'est le cas pour exit
    System.out.println("  " + String.format("%3s", l) + ": " + r + info.info.get(l) );
    for (Label s: r.succ()) printWithLife(visited, s, info);
  }
  /* imprime le graphe (pour debugger) */
  void print(Label entry) {
    print(new HashSet<Label>(), entry);
  }
}

interface ERTLVisitor {
  public void visit(ERconst o);
  public void visit(ERaccess_global o);
  public void visit(ERassign_global o);
  public void visit(ERload o);
  public void visit(ERstore o);
  public void visit(ERmunop o);
  public void visit(ERmbinop o);
  public void visit(ERmubranch o);
  public void visit(ERmbbranch o);
  public void visit(ERgoto o);
  public void visit(ERcall o);
  public void visit(ERalloc_frame o);
  public void visit(ERdelete_frame o);
  public void visit(ERget_param o);
  public void visit(ERpush_param o);
  public void visit(ERreturn o);
  public void visit(ERTLfun o);
  public void visit(ERTLfile o);
  }

class EmptyERTLERTLVisitor implements ERTLVisitor {
  public void visit(ERconst o) {}
  public void visit(ERaccess_global o) {}
  public void visit(ERassign_global o) {}
  public void visit(ERload o) {}
  public void visit(ERstore o) {}
  public void visit(ERmunop o) {}
  public void visit(ERmbinop o) {}
  public void visit(ERmubranch o) {}
  public void visit(ERmbbranch o) {}
  public void visit(ERgoto o) {}
  public void visit(ERcall o) {}
  public void visit(ERalloc_frame o) {}
  public void visit(ERdelete_frame o) {}
  public void visit(ERget_param o) {}
  public void visit(ERpush_param o) {}
  public void visit(ERreturn o) {}
  public void visit(ERTLfun o) {}
  public void visit(ERTLfile o) {}
  }

