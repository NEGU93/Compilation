package mini_c;

/** Opérations x86-64 utilisées pendant la sélection d'instructions */

/** opération x86-64 unaire */
abstract class Munop {} //TODO: implement this
class Maddi extends Munop { // Add integer
	int n;
	Maddi(int n) { this.n = n;}
	public String toString() { return "add $" + n; } 
}
class Msetei extends Munop {	// Set if equal (result == 0 ?)
	int n;
	Msetei(int n) { this.n = n;}
	public String toString() { return "sete $" + n; } 
}
class Msetnei extends Munop {	// Set if not equal
	int n;
	Msetnei(int n) { this.n = n;}
	public String toString() { return "setne $" + n; } 
}

/** opération x86-64 binaire */
enum Mbinop {
  Mmov
, Madd
, Msub
, Mmul
, Mdiv
, Msete
, Msetne
, Msetl
, Msetle
, Msetg
, Msetge
}

/** opération x86-64 de branchement (unaire) */
abstract class Mubranch {} 
class Mjz extends Mubranch {
	public String toString() { return "jz"; } 	
}
/* TODO: not yet implemented */
class Mjnz extends Mubranch {
	public String toString() { return "jnz"; } 	
}
class Mjlei  extends Mubranch {
	int n;
	Mjlei(int n) { this.n = n;}
	public String toString() { return "jle $" + n; } 	
}
class Mjgi extends Mubranch {
	int n;
	Mjgi(int n) { this.n = n;}
	public String toString() { return "jg $" + n; } 	
}

/** opération x86-64 de branchement (binaire) */
enum Mbbranch {
  	Mjl
	, Mjle
	, Mjeqeq
	, Mjneq
	, Mjg
	, Mjge
}
