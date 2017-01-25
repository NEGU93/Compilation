package mini_c;
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

class Constant {
	int c;
}	

/* expressions */

abstract class Expr {}

class Ecst extends Expr { // Integer
	final Constant c;
	Ecst(Constant c) {
		this.c = c;
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
}
class Eunop extends Expr { // Operation with only one Expr
	final Unop op;
	final Expr e;
	Eunop(Unop op, Expr e) {
		super();
		this.op = op;
		this.e = e;
	}
} 
class Ecall extends Expr { // <Identifier>(<Expr>*) ex. f(x);
	final String f;
	final LinkedList<Expr> l;
	Ecall(String f, LinkedList<Expr> l) {
		super();
		this.f = f;
		this.l = l;
	}
}

/* instruction */

abstract class Stmt {}

/*class Sif extends Stmt {
	final Expr e;
	final Stmt s1, s2;
	Sif(Expr e, Stmt s1, Stmt s2) {
		super();
		this.e = e;
		this.s1 = s1;
		this.s2 = s2;
	}
}*/
class Sreturn extends Stmt {
	final Expr e;

	Sreturn(Expr e) {
		super();
		this.e = e;
	}
}
/*
class Sprint extends Stmt {
	final Expr e;

	Sprint(Expr e) {
		super();
		this.e = e;
	}
}*/
class Sblock extends Stmt {
	final LinkedList<Stmt> l;
	Sblock() {
		this.l = new LinkedList<Stmt>();
	}
	Sblock(LinkedList<Stmt> l) {
		super();
		this.l = l;
	}
}
/*class Sfor extends Stmt {
	final String x;
	final Expr e;
	final Stmt s;
	Sfor(String x, Expr e, Stmt s) {
		super();
		this.x = x;
		this.e = e;
		this.s = s;
	}
}*/
class Seval extends Stmt {
	final Expr e;

	Seval(Expr e) {
		super();
		this.e = e;
	}
}

/* définition de fonction */

class Decl_function { 			// Declaration of a function
	final String f;
	final LinkedList<String> l; // arguments formels
	final Stmt s;
	Decl_function(String f, LinkedList<String> l, Stmt s) {
		super();
		this.f = f; 			// the functions name
		this.l = l; 			// arguments it has
		this.s = s; 			// what the function do
	}
}

class File {
	final LinkedList<Decl_function> l;
	final Stmt s;
	File(LinkedList<Decl_function> l, Stmt s) {
		super();
		this.l = l;
		this.s = s;
	}
}

