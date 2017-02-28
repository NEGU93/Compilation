package mini_c;

import java.util.LinkedList;
import java.util.ListIterator;

import static mini_c.Binop.Beqeq;
import static mini_c.Mbinop.Msetl;
import static mini_c.Mbinop.Msetle;
import static mini_c.Mbinop.Msetne;
import static mini_c.Unop.Unot;

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
	int c;

	public Constant(int c) {
		super();
		this.c = c;
	}
}

/* expressions */

abstract class Expr {
	abstract Label toRTL(Label l, Register r, RTLgraph g);
}

class Ecst extends Expr { // Integer
	final Constant c;

	Ecst(Constant c) {
		this.c = c;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g) {
		Rconst rc = new Rconst(this.c.c, r, l);
		return g.add(rc);		// Add item to the graph
	}
}

class Ebinop extends Expr { // Operation between 2 Expr
	final Binop op;
	final Expr e1, e2;

	Ebinop(Binop op, Expr e1, Expr e2) {
		super();
		this.op = op;
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g) {
		Register r2 = new Register();
		Rmbinop rb = new Rmbinop(Binop2Mbinop(), r2, r, l);
		Label L3 = g.add(rb);
		Label L2 = this.e1.toRTL(L3, r2, g);
		Label L1 = this.e2.toRTL(L2, r, g);
		return L1;
	}

	Mbinop Binop2Mbinop() {
		switch (this.op) {
			case Badd: return Mbinop.Madd;
			case Bsub: return Mbinop.Msub;
			case Bdiv: return Mbinop.Mdiv;
			case Bmul: return Mbinop.Mmul;
			case Bmod: return Mbinop.Mmov; 	// TODO: using mov as default for now
			case Beqeq: return Mbinop.Msete;
			case Bneq: return Msetne;
			case Blt: return Msetl;
			case Ble: return Msetle;
			case Bgt: return Mbinop.Msetg;
			case Bge: return Mbinop.Msetge;
			case Band: return Mbinop.Mmov; 	// TODO
			case Bor: return Mbinop.Mmov;	// TODO
			case Beq: return Mbinop.Msete;
			case Bobj: return Mbinop.Mmov; 	// TODO: this is struct->obj
		}
		return Mbinop.Mmov; // This should never happen but the IDE don't understand all the switches are covered.
	}

	Binop getOp() { return this.op; }
	Expr getE1() { return this.e1; }
	Expr getE2() { return this.e2; }
}

class Eunop extends Expr { // Operation with only one Expr
	final Unop op;
	final Expr e;

	Eunop(Unop op, Expr e) {
		super();
		this.op = op;
		this.e = e;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g) {
		Ecst c = new Ecst(new Constant(0)); // Corresponding to r2? maybe...
		Register r2 = new Register();
		Rmbinop rb = new Rmbinop(Unop2Mbinop(this.op), r2, r, l);
		Label L3 = g.add(rb);
		Label L2 = this.e.toRTL(L3, r2, g);
		Label L1 = c.toRTL(L2, r, g);
		return L1;
	}

	Mbinop Unop2Mbinop(Unop u) {
		switch(u) {
			case Uneg: return Mbinop.Msub;
			case Unot: return Mbinop.Msub;	// TODO: mmmm
		}
		return Mbinop.Mmov; // This should never happen but the IDE don't understand all the cases are covered.
	}

	Unop getOp() { return this.op; }
	Expr getE() { return this.e; }
}

class Ecall extends Expr { // <Identifier>(<Expr>*) ex. f(x);
	final String f;
	final LinkedList<Expr> l;

	Ecall(String f, LinkedList<Expr> l) {
		super();
		this.f = f;
		this.l = l;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g) {
		// TODO Auto-generated method stub
		return null;
	}
}

class Evar extends Expr {
	final String x;

	public Evar(String x) {
		this.x = x;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g) { // Here the variable is already created
		Rassign_global rv = new Rassign_global(r, this.x, l);
		return g.add(rv);	//TODO: not sure about this... the variable already exist... I add it again?
	}
}

class Type {
	final String t;

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
	abstract Label toRTL(Label l, Label ret, Register r, RTLgraph g);
}

class Sif extends Stmt {
	final Expr e;
	final Stmt s1, s2;

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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) {
		/* TODO: && and || not yet done */
		Label truel = s1.toRTL(l, ret, r, g);
		Label falsel = s2.toRTL(l, ret, r, g); // Should I put truel here or what?
		if (this.e instanceof Ebinop) {
			switch(((Ebinop) e).getOp()) {
				case Beqeq:
					return doMbbranch( Mbbranch.Mjeqeq, truel, falsel, r, g);
				case Bneq:
					return doMbbranch( Mbbranch.Mjneq, truel, falsel, r, g);
				case Blt:
					return doMbbranch( Mbbranch.Mjl, truel, falsel, r, g);
				case Ble:
					return doMbbranch( Mbbranch.Mjle, truel, falsel, r, g);
				case Bgt:
					return doMbbranch( Mbbranch.Mjg, truel, falsel, r, g);
				case Bge:
					return doMbbranch( Mbbranch.Mjge, truel, falsel, r, g);
				case Band:
					truel = new Sif( ((Ebinop) e).getE2(), s1, s2).toRTL(l, ret, r, g); // TODO: this must be different
					return doMubranch(new Mjz(), truel, falsel, r, g, ((Ebinop)e).getE1());
				case Bor:
					truel = new Sif( ((Ebinop) e).getE2(), s1, s2).toRTL(l, ret, r, g);
					return doMubranch(new Mjz(), truel, falsel, r, g, ((Ebinop)e).getE1());
			}
		}
		return doMubranch( new Mjz(), truel, falsel, r, g, this.e);
	}

	Label doMbbranch(Mbbranch m, Label truel, Label falsel, Register r, RTLgraph g) {
		Register r1 = r;
		Register r2 = new Register();
		Rmbbranch rb = new Rmbbranch(m, r1, r2, truel, falsel);
		Label L3 = g.add(rb);
		Label L2 = ((Ebinop)e).getE2().toRTL(L3, r2, g);
		Label L1 = ((Ebinop)e).getE1().toRTL(L2, r1, g);
		return L1;
	}

	Label doMubranch(Mubranch m, Label truel, Label falsel, Register r, RTLgraph g, Expr re) {
		Rmubranch rb = new Rmubranch(m, r, falsel, truel);
		Label L2 = g.add(rb);
		Label L1 = re.toRTL(L2, r, g);
		return L1;
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) {
		// TODO Auto-generated method stub
		return null;
	}
}

class Sreturn extends Stmt {
	final Expr e;

	Sreturn(Expr e) {
		super();
		this.e = e;
	}

	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) { //TODO: what happened with l?
		Label lab = this.e.toRTL(ret, r, g);
		return lab;
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) {
		// TODO Auto-generated method stub
		return null;
	}
}

/*
 * class Sfor extends Stmt { final String x; final Expr e; final Stmt s;
 * Sfor(String x, Expr e, Stmt s) { super(); this.x = x; this.e = e; this.s = s;
 * } }
 */
class Seval extends Stmt {
	final Expr e;

	Seval(Expr e) {
		super();
		this.e = e;
	}

	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) {	//TODO: what happened with l?
		Label lab = this.e.toRTL(ret, r, g);
		return lab;
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

	/*@Override
	Label toRTL(Label l, Register r, RTLgraph g) {
		Label L = l;
		ListIterator<String> listIterator = this.v.listIterator();
		while (listIterator.hasNext()) {
			Rassign_global rv = new Rassign_global(new Register(), listIterator.next(), l);    // TODO: what to do with the register?
			L = g.add(rv);
		}
		return L;
	}*/
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

	Decl_function(String f, LinkedList<Param> l, Sblock b, String t) throws Exception {
		super();
		this.f = f; // the functions name
		this.l = l; // arguments it has
		this.b = b; // what the function do
		this.r = new Type(t);
	}

	RTLfun toRTL() {
		RTLfun f = new RTLfun(this.f);
		LinkedList<Stmt> lstStmt = this.b.l;
		LinkedList<Decl_variable> lstVar = this.b.v;
		f.body = new RTLgraph();
		f.result = new Register();
		f.exit = new Label();
		Label current = f.exit;
		for (Stmt s : lstStmt) {
			current = s.toRTL(current, f.exit, f.result, f.body);
		}
		f.entry = current;
		return f;
	}
}

class Sizeof {
	final String s;

	public Sizeof(String s) {
		this.s = s;
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
			if (d instanceof Decl_function) {
				RTLfun f = ((Decl_function) d).toRTL();
				f.print();
				file.funs.add(f);
			}
		}
		return file;
	}

}
