package mini_c;

import java.util.*;

import static mini_c.Binop.*;
import static mini_c.Mbinop.*;

/* Syntaxe abstraite de Mini-Python */

/* opérateurs unaires et binaires */

enum Unop {
	Uneg, Unot
}

enum Binop {
	Badd, Bsub, Bmul, Bdiv, Bmod, Beqeq, Bneq, Blt, Ble, Bgt, Bge, // comparaison
																	// structurelle
	Band, Bor, Beq, Bobj /* Arrow stuff*/ // paresseux
}

/* constantes litérales */

class Constant {
	private int c;

	public Constant(int c) {
		super();
		this.c = c;
	}

	int getInt() { return c; }
}

/* expressions */

abstract class Expr {
	abstract Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables);
}

class Ecst extends Expr { // Integer
	final private Constant c;

	Ecst(Constant c) {
		this.c = c;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables) {
		Rconst rc = new Rconst(this.c.getInt(), r, l);
		return g.add(rc);		// Add item to the graph
	}

	int getInt() { return c.getInt(); }
}

class Ebinop extends Expr { // Operation between 2 Expr
	final private Binop op;
	final private Expr e1, e2;

	Ebinop(Binop op, Expr e1, Expr e2) {
		super();
		this.op = op;
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables) {
		switch (this.op) {
			case Beq: // x = y
				Label L2;
				if (e1 instanceof Evar) { // x is a variable
					if (variables.containsKey(((Evar) e1).getX())) {
						Rmbinop rmbinop = new Rmbinop(Mmov, r,variables.get(((Evar) e1).getX()), l);
						L2 = g.add(rmbinop);
					}
					else {
						Rassign_global rag = new Rassign_global(r, ((Evar) e1).getX(), l);
						L2 = g.add(rag);
					}
				}
				else if (e1 instanceof Ebinop) {	// p->x = y;
					Register r2 = new Register();
					int a = 0; //TODO
					Rstore rstore = new Rstore(r, r2, a, l); // r2 = p
					Label L3 = g.add(rstore);
					L2 = ((Ebinop) e1).getE1().toRTL(L3, r2, g, variables);
				}
				else { throw new Error("not lValue"); }
				Label L1 = this.e2.toRTL(L2, r, g, variables);
				return L1;
			case Bobj: // p->a (used to load only)
				Register r2 = new Register();
				int a = 0; // TODO
				Rload rs = new Rload(r2, r, a,  l); // r = p, r2 = new register and i = a (offset)
				Label L4 = g.add(rs);
				Label L3 = e1.toRTL(L4, r2, g, variables);
				return L3;
		}
		if ( (e2 instanceof Ecst) && (e1 instanceof Ecst)) { // e1 op 4
			switch (this.op) { // in this case, the compilator will transform things like 4 + 5 directly to 9 in compilation time to make it more effective!
				case Badd:
					return new Ecst(new Constant(((Ecst) e1).getInt() + ((Ecst) e2).getInt())).toRTL(l, r, g, variables);
				case Bsub:
					return new Ecst(new Constant(((Ecst) e1).getInt() - ((Ecst) e2).getInt())).toRTL(l, r, g, variables);
				case Bmul:
					return new Ecst(new Constant(((Ecst) e1).getInt() * ((Ecst) e2).getInt())).toRTL(l, r, g, variables);
				case Bdiv:
					return new Ecst(new Constant(((Ecst) e1).getInt() / ((Ecst) e2).getInt())).toRTL(l, r, g, variables);
				case Bmod:
					return new Ecst(new Constant(((Ecst) e1).getInt() % ((Ecst) e2).getInt())).toRTL(l, r, g, variables);
				case Beqeq:
					if (((Ecst) e1).getInt() == ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables);
					}
				case Beq:
					throw new Error("Constant = Constant not available");
				case Bneq:
					if (((Ecst) e1).getInt() != ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables);
					}
				case Blt:
					if (((Ecst) e1).getInt() < ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables);
					}
				case Ble:
					if (((Ecst) e1).getInt() <= ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables);
					}
				case Bgt:
					if (((Ecst) e1).getInt() > ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables);
					}
				case Bge:
					if (((Ecst) e1).getInt() >= ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables);
					}
				default:
					throw new Error("Incompatible operation between integers");
			}
		}
		else if ( (e1 instanceof Ecst) || (e2 instanceof Ecst) ) { //If i reach here then e1 && e2 are not both constants
			if ((this.op == Badd)) { // this is x + 4 or 4 + x
				if (e1 instanceof Ecst) {
					Rmunop rmunop = new Rmunop(new Maddi(((Ecst) e1).getInt()), r, l);
					Label L2 = g.add(rmunop);
					Label L1 = this.e2.toRTL(L2, r, g, variables);
					return L1;
				}
				else {
					Rmunop rmunop = new Rmunop(new Maddi(((Ecst) e2).getInt()), r, l);
					Label L2 = g.add(rmunop);
					Label L1 = this.e1.toRTL(L2, r, g, variables);
					return L1;
				}
			}
		}
		/* If I reached here then they are "normal" operations (+, /, *, -, <, >, etc ) and no 2 constants */
		Register r2 = new Register();
		Rmbinop rb = new Rmbinop(Binop2Mbinop(), r2, r, l);
		Label L3 = g.add(rb);
		Label L2 = this.e1.toRTL(L3, r2, g, variables);
		Label L1 = this.e2.toRTL(L2, r, g, variables);
		return L1;
	}

	private Mbinop Binop2Mbinop() {
		switch (this.op) {
			case Badd: return Mbinop.Madd;
			case Bsub: return Mbinop.Msub;
			case Bdiv: return Mbinop.Mdiv;
			case Bmul: return Mbinop.Mmul;
			case Beqeq: return Mbinop.Msete;
			case Bneq: return Msetne;
			case Blt: return Msetl;
			case Ble: return Msetle;
			case Bgt: return Mbinop.Msetg;
			case Bge: return Mbinop.Msetge;
			// This cases are made differently. And checked before getting here (Normally once inside this function this cannot happen)
			//case Bmod: return Mbinop.Mmov;
			//case Band: return Mbinop.Mmov;
			//case Bor: return Mbinop.Mmov;
			//case Beq: return Mbinop.Msete;
			// Bobj: return Mbinop.Mmov;
		}
		throw new Error("Binop operation that I don't know how to do");
	}
	Binop getOp() { return this.op; }
	Expr getE1() { return this.e1; }
	Expr getE2() { return this.e2; }
}

class Eunop extends Expr { // Operation with only one Expr
	final private Unop op;
	final private Expr e;

	Eunop(Unop op, Expr e) {
		super();
		this.op = op;
		this.e = e;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables) {
		switch(this.op) {
			case Uneg: /* The -a is done by making "0 - a" */
				Ecst c = new Ecst(new Constant(0));
				Register r2 = new Register();
				Rmbinop rb = new Rmbinop(Mbinop.Msub, r2, r, l);
				Label L3 = g.add(rb);
				Label L2 = this.e.toRTL(L3, r2, g, variables);
				Label L1 = c.toRTL(L2, r, g, variables);
				return L1;
			case Unot: /* if(true) return 0; else return 1; */
				//Not so fancy (nor effective) but it works...
				Sif sif = new Sif(this.e, new Seval(new Ecst(new Constant(0))), new Seval(new Ecst(new Constant(1))));
				return sif.toRTL(l, l, r, g, variables);
		}
		return new Label(); // This should never happen but the IDE and compiler don't understand all the cases are covered.
	}

	Unop getOp() { return this.op; }
	Expr getE() { return this.e; }
}

class Ecall extends Expr { // <Identifier>(<Expr>*) ex. f(x);
	final private String f;
	final private LinkedList<Expr> l;

	Ecall(String f, LinkedList<Expr> l) {
		super();
		this.f = f;
		this.l = l;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables) {
		LinkedList<Register> rl = new LinkedList<>();
		Rcall rc = new Rcall(r, this.f, rl, l);
		Label L = g.add(rc);
		for ( Expr expr : this.l) { // Save all the variables into registers
			r = new Register();
			L = expr.toRTL(L, r, g, variables);
			rl.add(r);
		}
		return L;
	}
}

class Evar extends Expr {
	final private String x;

	public Evar(String x) {
		this.x = x;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables) {// Here the variable is already created
		if (variables.containsKey(this.x)) {
			Rmbinop rmbinop = new Rmbinop(Mmov, variables.get(this.x), r, l);
			return g.add(rmbinop);
		}
		else {
			Raccess_global rv = new Raccess_global(x, r, l);
			return g.add(rv);
		}
	}

	String getX() { return this.x; }
}

class Type {
	final private String t;

	Type(String t) throws Exception {
		if ((t == "int") || (t == "struct")) {
			this.t = t;
		} else {
			throw new Exception("Type incorrect");
		}
	}
}

/* instruction */
abstract class Stmt {
	abstract Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables);
	// TODO: see if I have a constant and do the simpler jumps
	Label toRTLc(Expr ex, Label truel, Label falsel, Register r, RTLgraph g, Map<String, Register> variables) {
		if (ex instanceof Ebinop) {
			switch(((Ebinop) ex).getOp()) { // If there is a binary in the equation
				case Beqeq:	// a == 0
					return evaluate( Mbbranch.Mjeqeq, truel, falsel, r, g, (Ebinop)ex, variables);
				case Bneq: 	// a != 0
					return evaluate( Mbbranch.Mjneq, truel, falsel, r, g, (Ebinop)ex, variables);
				case Blt:	// a < 0
					return evaluate( Mbbranch.Mjl, truel, falsel, r, g, (Ebinop)ex, variables);
				case Ble:	// a <= 0
					return evaluate( Mbbranch.Mjle, truel, falsel, r, g, (Ebinop)ex, variables);
				case Bgt:	// a > 0
					return evaluate( Mbbranch.Mjg, truel, falsel, r, g, (Ebinop)ex, variables);
				case Bge:	// a >= 0
					return evaluate( Mbbranch.Mjge, truel, falsel, r, g, (Ebinop)ex, variables);
				case Band:	// a && 0
					return evaluate(new Mjz(), toRTLc(((Ebinop)ex).getE2(), truel, falsel, r, g, variables), falsel, r, g, ((Ebinop)ex).getE1(), variables);
				case Bor:	// a || 0
					return evaluate(new Mjz(), truel, toRTLc(((Ebinop)ex).getE2(), truel, falsel, r, g, variables), r, g, ((Ebinop)ex).getE1(), variables);
			}
		}	// If it's just a normal statement do the jz.
		if (ex instanceof Eunop) {
			switch(((Eunop) ex).getOp()) { // Kept in case I have to implement the Uneg in the future.
				case Unot: return evaluate( new Mjz(), falsel, truel, r, g, ex, variables); // Give them in the other sense because I will count the e part of !e.
			}
		}
		return evaluate( new Mjz(), truel, falsel, r, g, ex, variables);
	}
	private Label evaluate(Mbbranch m, Label truel, Label falsel, Register r, RTLgraph g, Ebinop e /* Only used for binoms */, Map<String, Register> variables) {
		Register r1 = r; // I know is redundant but I see it more clear... Hope the compiler is intelligent right?
		Register r2 = new Register();
		Rmbbranch rb = new Rmbbranch(m, r1, r2, truel, falsel);
		Label L3 = g.add(rb);
		Label L2 = e.getE2().toRTL(L3, r2, g, variables);
		Label L1 = e.getE1().toRTL(L2, r1, g, variables);
		return L1;
	}
	private Label evaluate(Mubranch m, Label truel, Label falsel, Register r, RTLgraph g, Expr re, Map<String, Register> variables) {
		// TODO: Because of the structure of machines. This may not be effective. Maybe it's better to use jnz
		Rmubranch rb = new Rmubranch(m, r, falsel, truel); // Turn around the true and false because it will be a jz (so in case it is zero I should do the false one)
		Label L2 = g.add(rb);
		if (re instanceof Eunop) {
			if (((Eunop) re).getOp() == Unop.Unot) {
				Label L1 = ((Eunop) re).getE().toRTL(L2, r, g, variables);
				return L1;
			}
		}
		Label L1 = re.toRTL(L2, r, g, variables); // This should not be called in the !re case.
		return L1;
	}
}

class Sif extends Stmt {
	final private Expr e;
	final private Stmt s1, s2;

	Sif(Expr e, Stmt s) {
		super();
		this.e = e;
		this.s1 = s;
		this.s2 = null;
	}

	Sif(Expr e, Stmt s1, Stmt s2) {
		super();
		this.e = e;
		this.s1 = s1;
		this.s2 = s2;
	}

	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables) {
		Label truel = s1.toRTL(l, ret, new Register(), g, variables);
		Label falsel = l;
		if (s2 != null) { falsel = s2.toRTL(l, ret, new Register(), g, variables); } // It will be null if I don't have the "else" statement.
		return toRTLc(this.e, truel, falsel, r, g, variables);
	}
}

class Swhile extends Stmt {
	final Expr e;
	final Stmt s;

	Swhile(Expr e, Stmt s) {
		super();
		this.e = e;
		this.s = s;
	}

	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables) {
		Label L = new Label();
		Label loop = this.s.toRTL(L, ret, new Register(), g, variables);
		Label Le = toRTLc(e, loop, l, r, g, variables);
		Rgoto rg = new Rgoto(Le);
		g.graph.put(L, rg);
		return Le;
	}
}

class Sreturn extends Stmt {
	final Expr e;

	Sreturn(Expr e) {
		super();
		this.e = e;
	}

	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables) {
		return this.e.toRTL(ret, r, g, variables); // I give ret (f.exit) because the return exit the function
	}
}

class Sblock extends Stmt {
	final LinkedList<Stmt> l;
	final LinkedList<Decl_variable> v;

	Sblock(LinkedList<Stmt> l, LinkedList<Decl_variable> v) {
		super();
		this.l = l;
		this.v = v;
	}

	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables) {
		Stmt s = null;
		do{
			try { s = this.l.removeLast(); }
			catch (NoSuchElementException e) { return l; }
			l = s.toRTL(l, ret, r, g, variables);
			r = new Register();
		} while(true);
	}
}

class Seval extends Stmt {
	final Expr e;

	Seval(Expr e) {
		super();
		this.e = e;
	}

	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables) {	// I send l because it's not a return.
		return this.e.toRTL(l, r, g, variables);
	}
}

/* Declarations */
class Declarations { }

class Decl_variable extends Declarations {
	final LinkedList<String> v;
	final Type t;

	Decl_variable(LinkedList<String> x) throws Exception {
		super();
		this.v = x;
		this.t = new Type("int");
	}

	Decl_variable(String x, LinkedList<String> l) throws Exception {
		super();
		this.v = l;
		this.t = new Type("struct");
	}

	void initializeVar(Set<Register> locals, Map<String, Register> variables) { // Initialize variables
		for ( String s : v ) {	// TODO: Still don't know where I save the string but well...
			Register r = new Register();
			variables.put(s, r);
			locals.add(r);
		}
	}
}

class Decl_struct extends Declarations {
	final String s;
	final LinkedList<Decl_variable> l;

	Decl_struct(String s, LinkedList<Decl_variable> l) {
		super();
		this.s = s;
		this.l = l;
	}
}

class Decl_function extends Declarations { // Declaration of a function
	final String f;
	final LinkedList<Param> l; // arguments formels
	final Sblock b;
	final Type r;
	LinkedList<Register> backUpReg; 

	Decl_function(String f, LinkedList<Param> l, Sblock b, String t) throws Exception {
		super();
		this.f = f; // the functions name
		this.l = l; // arguments it has
		this.b = b; // what the function do
		this.r = new Type(t);
		this.backUpReg = new LinkedList<>();
	}

	RTLfun toRTL() {
		RTLfun f = new RTLfun(this.f);
		LinkedList<Stmt> lstStmt = this.b.l; // List of statements
		LinkedList<Param> lstParam = this.l; // List of input parameters
		LinkedList<Decl_variable> lstVar = this.b.v; // List of declaration of variables
		f.body = new RTLgraph();
		f.exit = new Label();
		Label current = f.exit;
		/* Declaration of local Variables */
		for ( Param p: lstParam) {
			p.initializeVar(f.formals, f.variables);
		}
		for (Decl_variable dv : lstVar) {
			dv.initializeVar(f.locals, f.variables);
		}
		Stmt s = null;
		/* Do the statements */
		do{
			try { s = lstStmt.removeLast(); }
			catch (NoSuchElementException e) {
				f.entry = current;
				return f;
			}
			f.result = new Register(); // I create the next register to be used
			current = s.toRTL(current, f.exit, f.result, f.body, f.variables);
		} while(true);
	}
}

class Sizeof extends Expr {
	// TODO: sizeof yet not implemented
	final private String s;

	Sizeof(String s) {
		this.s = s;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables) {
		LinkedList<Expr> structSized = new LinkedList<>();
		structSized.add(new Evar(this.s));
		Ecall callSizeOf = new Ecall("sizeof", structSized);
		return callSizeOf.toRTL(l, r, g, variables);
	}
}

class Param {
	final Evar v;
	final Type t;

	public Param(Evar v, String s) throws Exception {
		this.v = v;
		this.t = new Type("struct");
	}

	public Param(Evar v) throws Exception {
		this.v = v;
		this.t = new Type("int");
	}

	void initializeVar(List<Register> formals, Map<String, Register> variables) { // Initialize variables
		Register r = new Register();
		variables.put(v.getX(), r);
		formals.add(r);
	}
}

/* File */
class File {
	final LinkedList<Declarations> l;

	File(LinkedList<Declarations> l) {
		super();
		this.l = l;
	}

	RTLfile toRTL() {
		RTLfile file = new RTLfile();
		for (Declarations d : this.l) {
			if (d instanceof Decl_variable) {
				for ( String s : ((Decl_variable) d).v ) {
					file.gvars.add(s);
				}
			}
			else if (d instanceof Decl_function) {
				RTLfun f = ((Decl_function) d).toRTL();
				f.print();
				file.funs.add(f);
			}
		}
		if (!file.gvars.isEmpty()) {
			System.out.print("Global Variables: ");
			for (String s : file.gvars) {
				System.out.print(" " + s + ",");
			}
			System.out.print("\n");
		}
		return file;
	}
}
