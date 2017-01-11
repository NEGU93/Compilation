package mini_python;
import java.util.LinkedList;

/* Syntaxe abstraite de Mini-Python */

/* opérateurs unaires et binaires */

enum Unop { Uneg, Unot }

enum Binop {
  Badd , Bsub , Bmul , Bdiv , Bmod,
  Beq , Bneq , Blt , Ble , Bgt , Bge, // comparaison structurelle
  Band , Bor // paresseux
}

/* constantes litérales */

abstract class Constant {
	static final Cnone None = new Cnone();
	abstract Value interp(); // la valeur d'une constante
}	

class Cnone extends Constant {
	@Override
	Value interp() { return new Vnone(); }
}
class Cbool extends Constant {
	final boolean b;
	Cbool(boolean b) {
		this.b = b;
	}
	@Override
	Value interp() { return new Vbool(this.b); }
}
class Cstring extends Constant {
	final String s;
	Cstring(String s) {
		this.s = s;
	}
	@Override
	Value interp() { return new Vstring(this.s); }
}
class Cint extends Constant {
	final int i; /* en Python les entiers sont en réalité de précision
                   arbitraire ; on simplifie ici */
	Cint(int i) {
		this.i = i;
	}
	@Override
	Value interp() { return new Vint(this.i); }
}

/* expressions */

abstract class Expr {
	abstract Value accept(Interpreter v);
}
class Ecst extends Expr {
	final Constant c;
	Ecst(Constant c) {
		this.c = c;
	}
	@Override
	Value accept(Interpreter v) { return v.interp(this); }
}
class Ebinop extends Expr {
	final Binop op;
	final Expr e1, e2;
	Ebinop(Binop op, Expr e1, Expr e2) {
		super();
		this.op = op;
		this.e1 = e1;
		this.e2 = e2;
	}
	@Override
	Value accept(Interpreter v) { return v.interp(this); }
}
class Eunop extends Expr {
	final Unop op;
	final Expr e;
	Eunop(Unop op, Expr e) {
		super();
		this.op = op;
		this.e = e;
	}
	@Override
	Value accept(Interpreter v) { return v.interp(this); }
}
class Ecall extends Expr {
	final String f;
	final LinkedList<Expr> l;
	Ecall(String f, LinkedList<Expr> l) {
		super();
		this.f = f;
		this.l = l;
	}
	@Override
	Value accept(Interpreter v) { return v.interp(this); }
}
class Elist extends Expr {
	final LinkedList<Expr> l;

	Elist(LinkedList<Expr> l) {
		super();
		this.l = l;
	}
	@Override
	Value accept(Interpreter v) { return v.interp(this); }
}
class Eleft extends Expr {
	final LeftValue lv;

	Eleft(LeftValue lv) {
		super();
		this.lv = lv;
	}
	@Override
	Value accept(Interpreter v) { return v.interp(this); }
}

/* valeur gauche */

abstract class LeftValue {
	abstract Value accept(Interpreter v); // as a right value
	abstract void assign(Interpreter v, Expr e);
}
class Lident extends LeftValue {
	final String s;

	Lident(String s) {
		super();
		this.s = s;
	}

	@Override
	Value accept(Interpreter v) { return v.interp(this); }
	@Override
	void assign(Interpreter v, Expr e) { v.assign(this, e); }
}
class Lnth extends LeftValue {
	final Expr e1, e2;
	Lnth(Expr e1, Expr e2) {
		super();
		this.e1 = e1;
		this.e2 = e2;
	}
	@Override
	Value accept(Interpreter v) { return v.interp(this); }
	@Override
	void assign(Interpreter v, Expr e) { v.assign(this, e); }
}

/* instruction */

abstract class Stmt {
	abstract void accept(Interpreter v) throws Return;
}
class Sif extends Stmt {
	final Expr e;
	final Stmt s1, s2;
	Sif(Expr e, Stmt s1, Stmt s2) {
		super();
		this.e = e;
		this.s1 = s1;
		this.s2 = s2;
	}
	@Override
	void accept(Interpreter v) throws Return { v.interp(this); }
}
class Sreturn extends Stmt {
	final Expr e;

	Sreturn(Expr e) {
		super();
		this.e = e;
	}
	@Override
	void accept(Interpreter v) throws Return { v.interp(this); }
}
class Sassign extends Stmt {
	final LeftValue lv;
	final Expr e;
	Sassign(LeftValue lv, Expr e) {
		super();
		this.lv = lv;
		this.e = e;
	}
	@Override
	void accept(Interpreter v) throws Return { v.interp(this); }
}
class Sprint extends Stmt {
	final Expr e;

	Sprint(Expr e) {
		super();
		this.e = e;
	}
	@Override
	void accept(Interpreter v) throws Return { v.interp(this); }
}
class Sblock extends Stmt {
	final LinkedList<Stmt> l;
	Sblock() {
		this.l = new LinkedList<Stmt>();
	}
	Sblock(LinkedList<Stmt> l) {
		super();
		this.l = l;
	}
	@Override
	void accept(Interpreter v) throws Return { v.interp(this); }
}
class Sfor extends Stmt {
	final String x;
	final Expr e;
	final Stmt s;
	Sfor(String x, Expr e, Stmt s) {
		super();
		this.x = x;
		this.e = e;
		this.s = s;
	}
	@Override
	void accept(Interpreter v) throws Return { v.interp(this); }
}
class Seval extends Stmt {
	final Expr e;

	Seval(Expr e) {
		super();
		this.e = e;
	}
	@Override
	void accept(Interpreter v) throws Return { v.interp(this); }
}

/* définition de fonction */

class Def {
	final String f;
	final LinkedList<String> l; // arguments formels
	final Stmt s;
	Def(String f, LinkedList<String> l, Stmt s) {
		super();
		this.f = f;
		this.l = l;
		this.s = s;
	}
}

class File {
	final LinkedList<Def> l;
	final Stmt s;
	File(LinkedList<Def> l, Stmt s) {
		super();
		this.l = l;
		this.s = s;
	}
}

/* un interprète (voir Interp.java) aura l'interface suivante */

interface Interpreter {
	// l'interprétation d'une expression est une valeur
	Value interp(Ecst e);
	Value interp(Ebinop e);
	Value interp(Eunop e);
	Value interp(Ecall e);
	Value interp(Elist e);
	Value interp(Eleft e);
	Value interp(Lident lv);
	Value interp(Lnth lv);
	// affectation
	void assign(Lident lv, Expr e);
	void assign(Lnth lv, Expr e);
	// l'interprétation d'une instruction ne renvoie rien
	// mais peut faire des effets de bord (affectations)
	// et lever l'exception Return
	void interp(Sif s) throws Return;
	void interp(Sreturn s) throws Return;
	void interp(Sassign s) throws Return;
	void interp(Sprint s) throws Return;
	void interp(Sblock s) throws Return;
	void interp(Sfor s) throws Return;
	void interp(Seval s) throws Return;
}

