package mini_c;

/* Register Transfer Language (RTL) */

// TODO: local and global variables
// TODO: I'm afraid, should I put a goto for the return part?

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static mini_c.Mbinop.Mmov;
import static mini_c.Register.callee_saved;
import static mini_c.Register.parameters;
import static mini_c.Register.result;

/** instruction RTL */

abstract class RTL {
  abstract void accept(RTLVisitor v);
  abstract Label[] succ();
  abstract ERTL toERTL(Label exit);
}

/** charge une constante dans un registre */
class Rconst extends RTL {
  int i;
  Register r;
  Label l;
  Rconst(int i, Register r, Label l) { this.i = i; this.r = r; this.l = l;  }
  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return "mov $" + i + " " + r + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERconst(this.i, result, exit); } // This will make it ERTL have rax if it's a return instruction!
    else { return new ERconst(this.i, this.r, this.l); }
  }
}

/** lit dans une variable globale */
class Raccess_global extends RTL {
  String s;
  Register r;
  Label l;

  Raccess_global(String s, Register r, Label l) { this.s = s; this.r = r; this.l = l;  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + s + " " + r + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERaccess_global(this.s, result, this.l);  }
    else { return new ERaccess_global(this.s, this.r, this.l); }
  }
}

/** écrit une variable globale */
class Rassign_global extends RTL {
  Register r;
  String s;
  Label l;

  Rassign_global(Register r, String s, Label l) { this.r = r; this.s = s; this.l = l;  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + r + " " + s + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERassign_global(this.r, this.s, this.l); } //TODO: Case return x = expr with x global. I whould return r. This is bad
    else { return new ERassign_global(this.r, this.s, this.l); }
  }
}

/** instruction mov i(r1), r2 */
class Rload extends RTL {
  Register r1;
  int i;
  Register r2;
  Label l;

  Rload(Register r1, Register r2, int i, Label l) {
	this.r1 = r1; this.i = i;
    this.r2 = r2; this.l = l;
  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + i + "(" + r1 + ") " + r2 + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERload(this.r1, this.i, result, this.l);  }
    else { return new ERload(this.r1, this.i, this.r2, this.l); }
  }
}

/** instruction mov r1, i(r2) */
class Rstore extends RTL {
  Register r1;
  Register r2;
  int i;
  Label l;

  Rstore(Register r1, Register r2, int i, Label l) { this.r1 = r1; this.r2 = r2; this.i = i; this.l = l;  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return "mov " + r1 + " " + i + "(" + r2 + ") " + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERstore(this.r1, this.r2, this.i, this.l);   } //TODO: case return r->i = expr; in which case I return expr == r1. So move r1 to rax.
    else { return new ERstore(this.r1, this.r2, this.i, this.l); }
  }
}

/** instruction x86-64 unaire */
class Rmunop extends RTL {
  Munop m;
  Register r;
  Label l;

  Rmunop(Munop m, Register r, Label l) { this.m = m; this.r = r; this.l = l; }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERmunop(this.m, result, this.l);   }
    else { return new ERmunop(this.m, this.r, this.l); }
  }
}

/** instruction x86-64 binaire */
class Rmbinop extends RTL {
  Mbinop m;
  Register r1;
  Register r2;
  Label l;

  Rmbinop(Mbinop m, Register r1, Register r2, Label l) { this.m = m; this.r1 = r1; this.r2 = r2; this.l = l;  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r1 + " " + r2 + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERmbinop(this.m, this.r1, result, this.l);   }
    else { return new ERmbinop(this.m, this.r1, this.r2, this.l); }
  }
}

/** instruction x86-64 de branchement (unaire) */
class Rmubranch extends RTL {
  Mubranch m;
  Register r;
  Label l1;
  Label l2;

  Rmubranch(Mubranch m, Register r, Label l1, Label l2) { this.m = m;
    this.r = r; this.l1 = l1; this.l2 = l2;  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r + " --> " + l1 + ", " + l2; }
  Label[] succ() { return new Label[] { l1, l2 }; }

  @Override
  ERTL toERTL(Label exit) {
    if ( (exit == l1) || (exit == l2) ) { throw new Error("I have no idea how I got here"); } // The fastest way to exit from a brunch is to put a return after, and that will give me another ERTL.
    else { return new ERmubranch(this.m, this.r, this.l1, this.l2); }
  }
}

/** instruction x86-64 de branchement (binaire) */
class Rmbbranch extends RTL {
  Mbbranch m;
  Register r1;
  Register r2;
  Label l1;
  Label l2;

  Rmbbranch(Mbbranch m, Register r1, Register r2, Label l1, Label l2) {
    this.m = m; this.r1 = r1; this.r2 = r2; this.l1 = l1; this.l2 = l2;
  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return m + " " + r1 + " " + r2 + " --> " + l1 + ", " + l2; }
  Label[] succ() { return new Label[] { l1, l2 }; }

  @Override
  ERTL toERTL(Label exit) {
    if ( (exit == l1) || (exit == l2) ) { throw new Error("I have no idea how I got here"); } // The fastest way to exit from a brunch is to put a return after, and that will give me another ERTL.
    else { return new ERmbbranch(this.m, this.r1, this.r2, this.l1, this.l2); }
  }
}

/** appel de fonction */
class Rcall extends RTL {
  Register r;
  String s;
  List<Register> rl;
  Label l;

  Rcall(Register r, String s, List<Register> rl, Label l) { this.r = r;
    this.s = s; this.rl = rl; this.l = l;  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return r + " <- call " + s + rl + " --> " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    return new ERcall(this.s, this.rl.size(), this.l); // There is no problem here because the return is in rax so I don't even check
  }

  ERTLgraph prevFun(ERTLgraph g, Label key) {
    Label L = this.l;
    // 4. pull if there was a push
    if (this.rl.size() > parameters.size()) { // if I have more parameters than registers available
      Maddi maddi = new Maddi(8 * (this.rl.size() - parameters.size()));
      ERmunop ermunop = new ERmunop(maddi, Register.rsp, L);
      L = g.add(ermunop);
      for (int i = 0; i < this.rl.size() - parameters.size(); i++) {
        ERload erload = new ERload(Register.rsp, i * 8, new Register(), L);
        L = g.add(erload);
      }
    }
    // 3. Get the result to %rax
    r = result;
    ERmbinop erb = new ERmbinop(Mmov, r, new Register(), L);
    L = g.add(erb);
    // 2. Call the function
    ERcall eRcall = new ERcall(this.s, this.rl.size(), L);
    if ( this.rl.size() > 0 ) { L = g.add(eRcall); }
    else { g.put(key, eRcall); }
    // 1. save the parameters to send
    for (int i = 0; i < this.rl.size(); i++) {
      if (i < parameters.size()) { 	// Using size instead of hardcoding a 6 to make it more general and prone to changes in code
        r = parameters.get(i);		// The first arguments in registers (
        ERmbinop eRmbinop = new ERmbinop(Mmov, this.rl.get(i), r, L);
        if ( (i == rl.size() - 1)  && (rl.size() <= parameters.size())) {
          g.put(key, eRmbinop);
        }
        else { L = g.add(eRmbinop); }
      }
      else { // The other arguments in the pile
        r = new Register();
        ERpush_param pushPam = new ERpush_param(r, L);
        Label L1 = g.add(pushPam);
        ERmbinop eRmbinop = new ERmbinop(Mmov, this.rl.get(i), r, L1);
        if ( i == rl.size() - 1) { g.put(key, eRmbinop); }
        else { L = g.add(eRmbinop); }
      }
    }
    return g;
  }
}

/** saut inconditionnel */
class Rgoto extends RTL {
  Label l;

  Rgoto(Label l) { this.l = l;  }

  void accept(RTLVisitor v) { v.visit(this); }
  public String toString() { return "goto " + l; }
  Label[] succ() { return new Label[] { l }; }

  @Override
  ERTL toERTL(Label exit) {
    if (exit == l) { return new ERgoto(this.l); } //TODO: can't find an example of when this can happen but I'm not sure it can't happen either.
    else { return new ERgoto(this.l); }
  }
}

/** une fonction RTL */

class RTLfun {
  /** nom de la fonction */
  String name;
  /** paramètres formels */
  List<Register> formals;
  /** résultat de la fonction */
  Register result;
  /** ensemble des variables locales */
  Set<Register> locals;
  /** point d'entrée dans le graphe */
  Label entry;
  /** point de sortie dans le graphe */
  Label exit;
  /** le graphe de flot de contrôle */
  RTLgraph body;
  /* backup registers for callee_saved (and to know where they are) */
  private LinkedList<Register> backUpReg;

  RTLfun(String name) { this.name = name; this.formals = new LinkedList<>(); this.locals = new HashSet<>(); }

  void accept(RTLVisitor v) { v.visit(this); }

  /** pour débugger */
  void print() {
	System.out.println("== RTL ==========================");
	System.out.println(result + " " + name + formals);
	System.out.println("  entry  : " + entry);
	System.out.println("  exit   : " + exit);
  System.out.println("  locals : " + locals);
	body.print(entry);
  }

  ERTLfun toERTL() {
    backUpReg = new LinkedList<>();
    ERTLfun efun = new ERTLfun(this.name, formals.size());
    efun.locals = this.locals;
    efun.body = this.body.toERTL(this.exit);         // RTL -> ERTL
    efun = startERTLGraph(efun);            // Add the begining of the function call
    efun.body = returnERTLGraph(efun.body); // Add the end of the function call
    efun.createLiveness();                  // Before going back I create the life
    efun.createInterference();
    return efun;
  }

  /* Make the start of the function */
  private ERTLfun startERTLGraph(ERTLfun efun) {
    Label current = this.entry;
    for (int i = 0; i < efun.formals; i++) {
      if (i < parameters.size()) { // Get the first arguments in registers
        ERmbinop erb = new ERmbinop(Mmov, parameters.get(i), new Register(), current);
        current = efun.body.add(erb);
      }
      else { // The other arguments in the pile
        ERget_param getPam = new ERget_param((i - parameters.size())/*8*/,new Register(), current); //TODO: do I have to put the 8?
        current = efun.body.add(getPam);
      }
    }
    for ( int i = 0 ; i < callee_saved.size(); i++ ) { // for every input
      backUpReg.add(new Register());
      ERmbinop er = new ERmbinop(Mmov, callee_saved.get(i), this.backUpReg.get(i), current);
      current = efun.body.add(er);
    }
    ERalloc_frame alloc = new ERalloc_frame(current);
    current = new Label();
    efun.body.put(current, alloc);
    efun.entry = current;
    return efun;
  }
  /* Make the return part. This function will take an already done graph and add the part for the return */
  private ERTLgraph returnERTLGraph(ERTLgraph eg) {
    ERreturn eRreturn = new ERreturn();
    Label current = eg.add(eRreturn);
    ERdelete_frame del = new ERdelete_frame(current);
    if (  callee_saved.size() == 0 ) {
      System.out.println("Warning: Check the Registers class, there are no callee saved");
      eg.put(this.exit, del);
    }
    else { current = eg.add(del); } // Normally this case should happen
    for ( int i = 0 ; i < callee_saved.size(); i++ ) {
      ERmbinop er2 = new ERmbinop(Mmov, this.backUpReg.get(i), callee_saved.get(i), current);
      if ( i == (callee_saved.size() - 1) ) { // The last one should go to exit kind of.
        eg.put(this.exit, er2);
      }
      else { current = eg.add(er2); }
    }
    return eg;
  }
}

/** un programme RTL */

class RTLfile {
  List<String> gvars;
  List<RTLfun> funs;

  RTLfile() {
    this.gvars = new LinkedList<String>();
    this.funs = new LinkedList<RTLfun>();
  }

  void accept(RTLVisitor v) { v.visit(this); }

  /** pour débugger */
  void print() {
	for (RTLfun fun: this.funs)
	  fun.print();
  }

  ERTLfile toERTL() {
    ERTLfile efile = new ERTLfile();
    for ( String gv : gvars) { efile.gvars.add(gv); } // Add global variables
    for ( RTLfun f : this.funs ) { efile.funs.add(f.toERTL()); }  // add the functions
    return efile;
  }
}

/** graphe de flot de contrôle (d'une fonction)
 *
 * c'est un dictionnaire qui associe une instruction de type RTL
 * à une étiquette de type Label
 */
class RTLgraph {
	Map<Label, RTL> graph = new HashMap<Label, RTL>();

	/** ajoute une nouvelle instruction dans le graphe
	  * et renvoie son étiquette */
	Label add(RTL instr) {
		Label l = new Label();
		graph.put(l, instr);
		return l;
	}

	// imprime le graphe par un parcours en profondeur
	private void print(Set<Label> visited, Label l) {
		if (visited.contains(l)) return;
		visited.add(l);
		RTL r = this.graph.get(l);
		if (r == null) return; // c'est le cas pour exit
		System.out.println("  " + String.format("%3s", l) + ": " + r);
		for (Label s: r.succ()) print(visited, s);
	}

	/** imprime le graphe (pour debugger) */
	void print(Label entry) {
		print(new HashSet<Label>(), entry);
	}

    ERTLgraph toERTL(Label exit) {
      ERTLgraph eg = new ERTLgraph();
      for ( Map.Entry<Label, RTL> g : this.graph.entrySet() ) {
        if (g.getValue() instanceof Rcall) {
          eg = ((Rcall) g.getValue()).prevFun(eg, g.getKey());
        }
        else {
          eg.put(g.getKey(), g.getValue().toERTL(exit)); // Wow this is cool. So perfect... XD
        }
      }
      return eg;
    }
}

/** visiteur pour parcourir la forme RTL
 * (pour la suite du compilateur)
 */

interface RTLVisitor {
  public void visit(Rconst o);
  public void visit(Raccess_global o);
  public void visit(Rassign_global o);
  public void visit(Rload o);
  public void visit(Rstore o);
  public void visit(Rmunop o);
  public void visit(Rmbinop o);
  public void visit(Rmubranch o);
  public void visit(Rmbbranch o);
  public void visit(Rcall o);
  public void visit(Rgoto o);
  public void visit(RTLfun o);
  public void visit(RTLfile o);
  }

/** un visiteur du code RTL qui ne fait rien */
class EmptyRTLVisitor implements RTLVisitor {
  public void visit(Rconst o) {}
  public void visit(Raccess_global o) {}
  public void visit(Rassign_global o) {}
  public void visit(Rload o) {}
  public void visit(Rstore o) {}
  public void visit(Rmunop o) {}
  public void visit(Rmbinop o) {}
  public void visit(Rmubranch o) {}
  public void visit(Rmbbranch o) {}
  public void visit(Rcall o) {}
  public void visit(Rgoto o) {}
  public void visit(RTLfun o) {}
  public void visit(RTLfile o) {}
}
