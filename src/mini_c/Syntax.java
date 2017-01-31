package mini_c;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import javax.crypto.spec.DESedeKeySpec;
import java.util.LinkedList;
import java.util.StringJoiner;

/* Syntaxe abstraite de Mini-Python */

/* opérateurs unaires et binaires */

enum Unop { Uneg, Unot }

enum Binop {
  Badd , Bsub , Bmul , Bdiv , Bmod,
  Beqeq , Bneq , Blt , Ble , Bgt , Bge, // comparaison structurelle
  Band , Bor, Beq, Bobj // paresseux
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
class Evar extends Expr {
	final String x;
	public Evar(String x) {
		this.x = x;
	}
}

class Type {
	final String t;
	Type(String t) throws Exception {
		if ((t == "int") || (t == "struct") ) { this.t = t; }
		else { throw new Exception("Type incorrect"); }
	}
}

/* instruction */
abstract class Stmt {}
class Sif extends Stmt {
	final Expr e;
	final LinkedList<Stmt> s1, s2;
	Sif(Expr e, LinkedList<Stmt> s) {
		super();
		this.e = e;
		this.s1 = s;
		this.s2 = new LinkedList<Stmt>();
	}
	Sif(Expr e, LinkedList<Stmt> s1, LinkedList<Stmt> s2) {
		super();
		this.e = e;
		this.s1 = s1;
		this.s2 = s2;
	}
}
class Swhile extends Stmt {
	final Expr e;
	final LinkedList<Stmt> s;
	Swhile(Expr e, LinkedList<Stmt> s) {
		super();
		this.e = e;
		this.s = s;
	}
}
class Sreturn extends Stmt {
	final Expr e;

	Sreturn(Expr e) {
		super();
		this.e = e;
	}
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
/* Declarations */
class Declarations {}
class Decl_variable extends Declarations {
	final Evar v;
	final Type t;
	final LinkedList<String> l;
	Decl_variable(String x) throws Exception {
		super();
		this.v = new Evar(x);
		this.t = new Type("int");
		this.l = new LinkedList<String>();
	}
	Decl_variable(String x, LinkedList<String> l) throws Exception {
		super();
		this.v = new Evar(x);
		this.t = new Type("struct");
		this.l = l;
	}
}
class Decl_struct extends Declarations {
	final String s;
	final LinkedList<Param> l;
	Decl_struct(String s, LinkedList<Param> l) {
		super();
		this.s = s;
		this.l = l;
	}
}
class Decl_function extends Declarations { 			// Declaration of a function
	final String f;
	final LinkedList<Param> l; // arguments formels
	final LinkedList<Stmt> s;
	final Type r;
	Decl_function(String f, LinkedList<Param> l, LinkedList<Stmt> s, String t) throws Exception {
		super();
		this.f = f; 			// the functions name
		this.l = l; 			// arguments it has
		this.s = s; 			// what the function do
		this.r = new Type(t);
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
}

