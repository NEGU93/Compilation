package mini_c;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.List;

import static mini_c.Binop.Bmod;
import static mini_c.Mbinop.*;
import static mini_c.Register.callee_saved;
import static mini_c.Register.parameters;
import static mini_c.Register.result;

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
	abstract Label toRTL(Label l, Register r, RTLgraph g);
	abstract Label toERTL(Label l, Register r, ERTLgraph g);
}

class Ecst extends Expr { // Integer
	final private Constant c;

	Ecst(Constant c) {
		this.c = c;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g) {
		Rconst rc = new Rconst(this.c.getInt(), r, l);
		return g.add(rc);		// Add item to the graph
	}

	@Override
	Label toERTL(Label l, Register r, ERTLgraph g) {
		ERconst eRconst = new ERconst(this.c.getInt(), r, l);
		return g.add(eRconst);
	}
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
	Label toRTL(Label l, Register r, RTLgraph g) {
		// TODO: check if I have constants to make it faster. (do add $1 #r instead of charging 1 to a register and add both registers)
		switch (this.op) {
			case Beq: // x = y
				Label L2;
				if (e1 instanceof Evar) { // x is a variable
					Rassign_global rag = new Rassign_global(r, ((Evar) e1).getX(), l);
					L2 = g.add(rag);
				}
				else if (e1 instanceof Ebinop) {	// p->x = y;
					Register r2 = new Register();
					int a = 0; //TODO
					Rstore rstore = new Rstore(r, r2, a, l); // r2 = p
					Label L3 = g.add(rstore);
					L2 = ((Ebinop) e1).getE1().toRTL(L3, r2, g);
				}
				else { throw new Error("not lValue"); }
				Label L1 = this.e2.toRTL(L2, r, g);
				return L1;
			case Bobj: // p->a (used to load only)
				Register r2 = new Register();
				int a = 0; // TODO
				Rload rs = new Rload(r2, r, a,  l); // r = p, r2 = new register and i = a (offset)
				Label L4 = g.add(rs);
				Label L3 = e1.toRTL(L4, r2, g);
				return L3;
		}
		/* If I reached here then they are "normal" operations (+, /, *, -, <, >, etc )*/
		Register r2 = new Register();
		Rmbinop rb = new Rmbinop(Binop2Mbinop(), r2, r, l);
		Label L3 = g.add(rb);
		Label L2 = this.e1.toRTL(L3, r2, g);
		Label L1 = this.e2.toRTL(L2, r, g);
		return L1;
	}

	@Override
	Label toERTL(Label l, Register r, ERTLgraph g) {
		switch (this.op) {
			case Beq: // x = y
				Label L2;
				if (e1 instanceof Evar) { // x is a variable
					ERassign_global rag = new ERassign_global(r, ((Evar) e1).getX(), l);
					L2 = g.add(rag);
				}
				else if (e1 instanceof Ebinop) {	// p->x = y;
					Register r2 = new Register();
					int a = 0; //TODO
					ERstore rstore = new ERstore(r, r2, a, l); // r2 = p
					Label L3 = g.add(rstore);
					L2 = ((Ebinop) e1).getE1().toERTL(L3, r2, g);
				}
				else { throw new Error("not lValue"); }
				Label L1 = this.e2.toERTL(L2, r, g);
				return L1;
			case Bobj: // p->a (used to load only)
				Register r2 = new Register();
				int a = 0; // TODO
				ERload rs = new ERload(r2, a, r,  l); // r = p, r2 = new register and i = a (offset)
				Label L4 = g.add(rs);
				Label L3 = e1.toERTL(L4, r2, g);
				return L3;
		}
		Register r1 = r;
		Register r2 = new Register();
		ERmbinop erb = new ERmbinop(Binop2Mbinop(), r1, r2, l);
		Label L3 = g.add(erb);
		Label L2 = this.e1.toERTL(L3, r2 ,g);
		Label L1 = this.e2.toERTL(L2, r1, g);
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
			//default: return Mbinop.Mmov;
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
	Label toRTL(Label l, Register r, RTLgraph g) {
		switch(this.op) {
			case Uneg: /* The -a is done by making "0 - a" */
				Ecst c = new Ecst(new Constant(0));
				Register r2 = new Register();
				Rmbinop rb = new Rmbinop(Mbinop.Msub, r2, r, l);
				Label L3 = g.add(rb);
				Label L2 = this.e.toRTL(L3, r2, g);
				Label L1 = c.toRTL(L2, r, g);
				return L1;
			case Unot: /* if(true) return 0; else return 1; */
				//Not so fancy (nor effective) but it works...
				Sif sif = new Sif(this.e, new Seval(new Ecst(new Constant(0))), new Seval(new Ecst(new Constant(1))));
				return sif.toRTL(l, l, r, g);
		}
		return new Label(); // This should never happen but the IDE and compiler don't understand all the cases are covered.
	}

	@Override
	Label toERTL(Label l, Register r, ERTLgraph g) {
		switch (this.op) {
			case Uneg: /* The -a is done by making "0 - a" */
				Ecst c = new Ecst(new Constant(0));
				Register r2 = new Register();
				ERmbinop rb = new ERmbinop(Mbinop.Msub, r2, r, l);
				Label L3 = g.add(rb);
				Label L2 = this.e.toERTL(L3, r2, g);
				Label L1 = c.toERTL(L2, r, g);
				return L1;
			case Unot: // if(true) return 0; else return 1;
				//Not so fancy (nor effective) but it works...
				Sif sif = new Sif(this.e, new Seval(new Ecst(new Constant(0))), new Seval(new Ecst(new Constant(1))));
				return sif.toERTL(l, g);
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
	Label toRTL(Label l, Register r, RTLgraph g) {
		LinkedList<Register> rl = new LinkedList<>();
		Rcall rc = new Rcall(r, this.f, rl, l);
		Label L = g.add(rc);
		for ( Expr expr : this.l) { // Save all the variables into registers
			r = new Register();
			L = expr.toRTL(L, r, g);
			rl.add(r);
		}
		return L;
	}

	@Override
	Label toERTL(Label l, Register r, ERTLgraph g) {
		//LinkedList<Register> rl = new LinkedList<>();
		/* 4. pull if there was a push */
		if (this.l.size() > parameters.size()) { // if I have more parameters than registers available
			Maddi maddi = new Maddi(8 * (this.l.size() - parameters.size()));
			ERmunop ermunop = new ERmunop(maddi, Register.rsp, l);
			l = g.add(ermunop);
			for (int i = 0; i < this.l.size() - parameters.size(); i++) {
				ERload erload = new ERload(Register.rsp, i * 8, new Register(), l);
				l = g.add(erload);
			}
		}
		/* 3. Get the result to %rax */
		r = result;
		ERmbinop erb = new ERmbinop(Mmov, r, new Register(), l); 
		l = g.add(erb);
		/* 2. Call the function */
		ERcall eRcall = new ERcall(this.f, this.l.size(), l);
		Label L = g.add(eRcall);
		/* 1. save the parameters to send */
		for (int i = 0; i < this.l.size(); i++) {
			if (i < parameters.size()) { 	// Using size instead of hardcoding a 6 to make it more general and prone to changes in code
				r = parameters.get(i);		// The first arguments in registers (
				L = this.l.get(i).toERTL(L, r, g);
			}
			else { // The other arguments in the pile
				r = new Register();
				ERpush_param pushPam = new ERpush_param(r, L);
				Label L1 = g.add(pushPam);
				L = this.l.get(i).toERTL(L1, r, g);
			}
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
	Label toRTL(Label l, Register r, RTLgraph g) { // Here the variable is already created
		Raccess_global rv = new Raccess_global(x, r, l);
		return g.add(rv);
	}

	@Override
	Label toERTL(Label l, Register r, ERTLgraph g) {
		ERaccess_global rv = new ERaccess_global(x, r, l);
		return g.add(rv);
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
	abstract Label toRTL(Label l, Label ret, Register r, RTLgraph g);
	abstract Label toERTL(Label l, ERTLgraph g);

	Label toRTLc(Expr ex, Label truel, Label falsel, Register r, RTLgraph g) {
		if (ex instanceof Ebinop) {
			switch(((Ebinop) ex).getOp()) { // If there is a binary in the equation
				case Beqeq:	// a == 0
					return evaluate( Mbbranch.Mjeqeq, truel, falsel, r, g, (Ebinop)ex);
				case Bneq: 	// a != 0
					return evaluate( Mbbranch.Mjneq, truel, falsel, r, g, (Ebinop)ex);
				case Blt:	// a < 0
					return evaluate( Mbbranch.Mjl, truel, falsel, r, g, (Ebinop)ex);
				case Ble:	// a <= 0
					return evaluate( Mbbranch.Mjle, truel, falsel, r, g, (Ebinop)ex);
				case Bgt:	// a > 0
					return evaluate( Mbbranch.Mjg, truel, falsel, r, g, (Ebinop)ex);
				case Bge:	// a >= 0
					return evaluate( Mbbranch.Mjge, truel, falsel, r, g, (Ebinop)ex);
				case Band:	// a && 0
					return evaluate(new Mjz(), toRTLc(((Ebinop)ex).getE2(), truel, falsel, r, g), falsel, r, g, ((Ebinop)ex).getE1());
				case Bor:	// a || 0
					return evaluate(new Mjz(), truel, toRTLc(((Ebinop)ex).getE2(), truel, falsel, r, g), r, g, ((Ebinop)ex).getE1());
			}
		}	// If it's just a normal statement do the jz.
		if (ex instanceof Eunop) {
			switch(((Eunop) ex).getOp()) { // Kept in case I have to implement the Uneg in the future.
				case Unot: return evaluate( new Mjz(), falsel, truel, r, g, ex); // Give them in the other sense because I will count the e part of !e.
			}
		}
		return evaluate( new Mjz(), truel, falsel, r, g, ex);
	}
	Label toERTLc(Expr ex, Label truel, Label falsel, Register r, ERTLgraph g) {
		if (ex instanceof Ebinop) {
			switch(((Ebinop) ex).getOp()) { // If there is a binary in the equation
				case Beqeq:	// a == 0
					return evaluate( Mbbranch.Mjeqeq, truel, falsel, r, g, (Ebinop)ex);
				case Bneq: 	// a != 0
					return evaluate( Mbbranch.Mjneq, truel, falsel, r, g, (Ebinop)ex);
				case Blt:	// a < 0
					return evaluate( Mbbranch.Mjl, truel, falsel, r, g, (Ebinop)ex);
				case Ble:	// a <= 0
					return evaluate( Mbbranch.Mjle, truel, falsel, r, g, (Ebinop)ex);
				case Bgt:	// a > 0
					return evaluate( Mbbranch.Mjg, truel, falsel, r, g, (Ebinop)ex);
				case Bge:	// a >= 0
					return evaluate( Mbbranch.Mjge, truel, falsel, r, g, (Ebinop)ex);
				case Band:	// a && 0
					return evaluate(new Mjz(), toERTLc(((Ebinop)ex).getE2(), truel, falsel, r, g), falsel, r, g, ((Ebinop)ex).getE1());
				case Bor:	// a || 0
					return evaluate(new Mjz(), truel, toERTLc(((Ebinop)ex).getE2(), truel, falsel, r, g), r, g, ((Ebinop)ex).getE1());
			}
		}	// If it's just a normal statement do the jz.
		if (ex instanceof Eunop) {
			switch(((Eunop) ex).getOp()) { // Kept in case I have to implement the Uneg in the future.
				case Unot: return evaluate( new Mjz(), falsel, truel, r, g, ex); // Give them in the other sense because I will count the e part of !e.
			}
		}
		return evaluate( new Mjz(), truel, falsel, r, g, ex);
	}
	private Label evaluate(Mbbranch m, Label truel, Label falsel, Register r, RTLgraph g, Ebinop e /* Only used for binoms */) {
		Register r1 = r; // I know is redundant but I see it more clear... Hope the compiler is intelligent right?
		Register r2 = new Register();
		Rmbbranch rb = new Rmbbranch(m, r1, r2, truel, falsel);
		Label L3 = g.add(rb);
		Label L2 = e.getE2().toRTL(L3, r2, g);
		Label L1 = e.getE1().toRTL(L2, r1, g);
		return L1;
	}
	private Label evaluate(Mubranch m, Label truel, Label falsel, Register r, RTLgraph g, Expr re) {
		// TODO: Because of the structure of machines. This may not be effective. Maybe it's better to use jnz
		Rmubranch rb = new Rmubranch(m, r, falsel, truel); // Turn around the true and false because it will be a jz (so in case it is zero I should do the false one)
		Label L2 = g.add(rb);
		if (re instanceof Eunop) {
			if (((Eunop) re).getOp() == Unop.Unot) {
				Label L1 = ((Eunop) re).getE().toRTL(L2, r, g);
				return L1;
			}
		}
		Label L1 = re.toRTL(L2, r, g); // This should not be called in the !re case.
		return L1;
	}
	private Label evaluate(Mubranch m, Label truel, Label falsel, Register r, ERTLgraph g, Expr re) {
		ERmubranch rb = new ERmubranch(m, r, falsel, truel); // Turn around the true and false because it will be a jz (so in case it is zero I should do the false one)
		Label L2 = g.add(rb);
		if (re instanceof Eunop) {
			if (((Eunop) re).getOp() == Unop.Unot) {
				Label L1 = ((Eunop) re).getE().toERTL(L2, r, g);
				return L1;
			}
		}
		Label L1 = re.toERTL(L2, r, g); // This should not be called in the !re case.
		return L1;
	}
	private Label evaluate(Mbbranch m, Label truel, Label falsel, Register r, ERTLgraph g, Ebinop e /* Only used for binoms */) {
		Register r1 = r; // I know is redundant but I see it more clear... Hope the compiler is intelligent right?
		Register r2 = new Register();
		ERmbbranch rb = new ERmbbranch(m, r1, r2, truel, falsel);
		Label L3 = g.add(rb);
		Label L2 = e.getE2().toERTL(L3, r2, g);
		Label L1 = e.getE1().toERTL(L2, r1, g);
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) {
		Label truel = s1.toRTL(l, ret, new Register(), g);
		Label falsel = l;
		if (s2 != null) { falsel = s2.toRTL(l, ret, new Register(), g); } // It will be null if I don't have the "else" statement.
		return toRTLc(this.e, truel, falsel, r, g);
	}

	@Override
	Label toERTL(Label l, ERTLgraph g) {
		Label truel = s1.toERTL(l, g);
		Label falsel = l;
		if (s2 != null) { falsel = s2.toERTL(l, g); } // It will be null if I don't have the "else" statement.
		return toERTLc(this.e, truel, falsel, new Register(), g);
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
		Label L = new Label();
		Label loop = this.s.toRTL(L, ret, new Register(), g);
		Label Le = toRTLc(e, loop, l, r, g);
		Rgoto rg = new Rgoto(Le);
		g.graph.put(L, rg);
		return Le;
	}

	@Override
	Label toERTL(Label l, ERTLgraph g) {
		Label L = new Label();
		Label loop = this.s.toERTL(L, g);
		Label Le = toERTLc(e, loop, l, new Register(), g);
		ERgoto rg = new ERgoto(Le);
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) {
		return this.e.toRTL(ret, r, g); // I give ret (f.exit) because the return exit the function
	}

	@Override
	Label toERTL(Label l, ERTLgraph g) {
		/*ERreturn eRreturn = new ERreturn();
		Label L = g.add(eRreturn);
		// TODO: Recover the callee_saved
		ERdelete_frame del = new ERdelete_frame(L);
		L = g.add(del);*/
		return this.e.toERTL(l, Register.rax, g);
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
		Stmt s = null;
		do{
			try { s = this.l.removeLast(); }
			catch (NoSuchElementException e) { return l; }
			l = s.toRTL(l, ret, r, g);
			r = new Register();
		} while(true);
	}

	@Override
	Label toERTL(Label l, ERTLgraph g) {
		Stmt s = null;
		do{
			try { s = this.l.removeLast(); }
			catch (NoSuchElementException e) { return l; }
			l = s.toERTL(l, g);
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g) {	// I send l because it's not a return.
		return this.e.toRTL(l, r, g);
	}

	@Override
	Label toERTL(Label l, ERTLgraph g) {
		return this.e.toERTL(l, new Register(), g);
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

	void initializeVar(Set<Register> locals) { // Initialize variables
		for ( String s : v ) {	// Still don't know where I save the string but well...
			locals.add(new Register());
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
			p.initializeVar(f.formals);
		}
		for (Decl_variable dv : lstVar) {
			dv.initializeVar(f.locals);
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
			current = s.toRTL(current, f.exit, f.result, f.body);
		} while(true);
	}

	ERTLfun toERTL() {
		ERTLfun f = new ERTLfun(this.f, l.size()); // The second argument is the total amount of arguments
		LinkedList<Stmt> lstStmt = this.b.l; // List of statements
		LinkedList<Decl_variable> lstVar = this.b.v; // List of declaration of variables
		f.body = new ERTLgraph();
		Label current = new Label();
		for (Decl_variable dv : lstVar) {
			dv.initializeVar(f.locals);
		}
		Stmt s = null;
		/* Make the return part */
		ERreturn eRreturn = new ERreturn();
		current = f.body.add(eRreturn);
		ERdelete_frame del = new ERdelete_frame(current);
		current = f.body.add(del);
		for (Register r : callee_saved) {
			this.backUpReg.add(new Register());
			ERmbinop er2 = new ERmbinop(Mmov, this.backUpReg.getLast(), r, current);
			current = f.body.add(er2);
		}
		final Label returnLabel = current;
		
		do{
			try { s = lstStmt.removeLast(); }
			catch (NoSuchElementException e) {
				for (int i = 0; i < f.formals; i++) {
					if (i < parameters.size()) { // Get the first arguments in registers
						ERmbinop erb = new ERmbinop(Mmov, parameters.get(i), new Register(), current);
						current = f.body.add(erb);
					}
					else { // The other arguments in the pile
						ERget_param getPam = new ERget_param(i - parameters.size(),new Register(), current);
						current = f.body.add(getPam);
					}
				}
				for ( int i = 0 ; i < this.backUpReg.size(); i++ ) { // for every input
					ERmbinop er = new ERmbinop(Mmov, callee_saved.get(i), this.backUpReg.get(i), current);
					current = f.body.add(er);
				}
				ERalloc_frame alloc = new ERalloc_frame(current);
				current = f.body.add(alloc);
				f.entry = current;
				return f;
			}
			if (s instanceof Sreturn) { current = returnLabel; }
			current = s.toERTL(current, f.body);
		} while(true);
	}
}

class Sizeof extends Expr {
	final private String s;

	Sizeof(String s) {
		this.s = s;
	}

	@Override
	Label toRTL(Label l, Register r, RTLgraph g) {
		LinkedList<Expr> structSized = new LinkedList<>();
		structSized.add(new Evar(this.s));
		Ecall callSizeOf = new Ecall("sizeof", structSized);
		return callSizeOf.toRTL(l, r, g);
	}

	@Override
	Label toERTL(Label l, Register r, ERTLgraph g) {
		return null;
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

	void initializeVar(List<Register> formals) { // Initialize variables
		formals.add(new Register());
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

	ERTLfile toERTL() {
		ERTLfile file = new ERTLfile();
		for (Declarations d : this.l) {
			if (d instanceof Decl_function) {
				ERTLfun f = ((Decl_function) d).toERTL();
				f.print();
				file.funs.add(f);
			}
		}
		return file;
	}
}
