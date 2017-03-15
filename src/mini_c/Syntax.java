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
class Typing {
	  public static HashMap<String,LinkedList<Param>> declStruct = new HashMap<String,LinkedList<Param>>(); // there we store the list of pointers a structure contains
	  public static HashMap<String,Var> varType = new HashMap<String, Var>(); //there we store the variable declared as int, the value doesn't serve anything
	  //HashMap<String,String> varsPoints = new HashMap<String, String>(); //there we store the struct id * vars, the key is the name of the pointer, the value is the id
	  public static HashMap<String,LinkedList<Var>> funArgsType=new HashMap<String,LinkedList<Var>>(); //we store, for each function the list of the type of its arguments
	  //HashMap<String,String> funType=new HashMap<String,String>();
	  public static HashMap<String,Var> varTypeLoc = new HashMap<String, Var>();

	  Typing() {
		  varType.put("putchar", new Var("putchar","int"));
		  funArgsType.put("putchar",new LinkedList<Var>());
		  funArgsType.get("putchar").add(new Var("","int"));
		  varType.put("sbrk", new Var("sbrk","void*"));
		  funArgsType.put("sbrk",new LinkedList<Var>());
		  funArgsType.get("sbrk").add(new Var("","int"));
	  }

	  public static boolean equalsType(String x, String y) {
		  if (x.equals(y)) return true;
		  if (x.equals("typenull")) {
			 if (!y.equals("void*")) return true;
		  }
		  if (x.equals("void*")) {
				 if (!y.equals("int")) return true;
			  }
		  if (y.equals("typenull")) {
			 if (!x.equals("void*")) return true;
		  }
		  if (y.equals("void*")) {
				 if (!x.equals("int")) return true;
			  }
		  return false;
	  }
	}

/* constantes litérales */

class Constant {
	private int c;

	public Constant(int c) {
		super();
		this.c = c;
	}

	int getInt() { return c; }
	  String Typer() {
			if (this.c==0){
				return("typenull");
			}
		    return("int");
		  }
}

/* expressions */

abstract class Expr {
	abstract Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables);
	abstract String Typer();
}

class Ecst extends Expr { // Integer
	final private Constant c;

	Ecst(Constant c) {
		this.c = c;
	}
	  @Override
	  String Typer() {
	    return this.c.Typer();
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
	  String Typer() {
	   String t1 = this.e1.Typer();
	   String t2;
	    switch (this.op) {
	      case Bobj:
	      //if (this.e1 instanceof Evar)
	      if (Typing.declStruct.get(t1)!=null){
	       // String x1 = ((Evar)this.e1).x;
	        if (this.e2 instanceof Evar) {
	          String x2 = ((Evar)this.e2).x;
	          //System.out.println("this x2 is "+ x2);
	          if (Typing.declStruct.containsKey(t1)) {
	            //int n = Typing.declStruct.get(((Evar)this.e1).x).indexOf();
	            for (Param p :Typing.declStruct.get(t1)) {
	            	//System.out.println("field "+p.v);
	              if (x2.equals(p.v.x)) {
	                return(p.t.t);
	              }
	            }
	            throw new Error("The field "+x2+" does not exist");
	          }
	          else {
	            throw new Error("The structure" + t1 + "does no exist");
	          }
	        }
	        else {
	          throw new Error("This is not a field");
	        }

	        }
	        else {
	          throw new Error("Not a structure");
	        }

	      case Beqeq:
	    	t2 = this.e2.Typer();
	        if (Typing.equalsType(t1,t2)) {

	          return (t1);
	        }
	        else {throw new Error("Bad type expression, left term is "+ t1+" and and right term is "+t2);}
	      case Bneq :
	      case Blt:
	      case Bgt:
	      case Ble:
	      case Bge:
	    	t2 = this.e2.Typer();
	        if (Typing.equalsType(t1,t2)) {
	          return ("int");
	        }
	        else {throw new Error("Bad type expression, t1 is "+t1+" and t2 is "+t2);}
	      case Bor:
	      case Band:
	    	t2 = this.e2.Typer();
	        return("int");
	      case Badd:
	      case Bmul:
	      case Bsub:
	      case Bdiv:
	    	t2 = this.e2.Typer();
	        if (Typing.equalsType("int",t1) && Typing.equalsType(t2,"int")) {
	          return ("int");
	        }
	        else {throw new Error("Bad type, operation impossible");}
	      case Beq:
	    	  t2 = this.e2.Typer();
	          if (this.e1 instanceof Evar) {
	        	//if (this.e2 instanceof )
	            if (Typing.equalsType(t1,t2)) {
	              return t1;
	            }
	            else {throw new Error("invalid type type left is "+t1+" and type right is "+t2);}
	          }
	          else if (this.e1 instanceof Ebinop) {
	            switch(((Ebinop) this.e1).op){
	              case Bobj:
	            	 // System.out.println("Le type 1 est "+t1+" et le type 2 est "+t2);
	                if (Typing.equalsType(t1,t2)) {
	                  return t1;
	                }
	                else {throw new Error("invalid type");}
	              default:
	              throw new Error("invalid type");
	            }
	          }
	          else {
	            throw new Error("invalid type");
	          }
	    }
	    return("");
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
	  String Typer() {
	    switch (this.op) {
	      case Uneg:
	        if (this.e.Typer()=="int") {
	          return("int");
	        }
	        else {throw new Error("Invalid expression for negation");}
	      case Unot:
	        String s=this.e.Typer();
	        return("int");
	    }
	    return ("");
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
	  String Typer() {
	    if (Typing.varType.containsKey(this.f)) {
	      LinkedList<Var> typeArgs= Typing.funArgsType.get(this.f);
	      Iterator<Var> it= typeArgs.iterator();
	      if (typeArgs.size()!=this.l.size()) {
	    	  throw new Error("invalid number of arguments");
	      }
	      for (Expr e:this.l) {
	        if (!Typing.equalsType(e.Typer(),it.next().type)) {
	          throw new Error("invalid argument type");
	        }
	      }
	      return (Typing.varType.get(this.f).type);
	    }
	    else {
	      throw new Error("This function does not exist");
	    }
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
	final  String x;

	public Evar(String x) {
		this.x = x;
	}
	  @Override
	  String Typer() {
	    if (Typing.varTypeLoc.get(this.x)!= null) {
	      return (Typing.varTypeLoc.get(this.x).type);
	    }
	    else if (Typing.varType.get(this.x)!=null) {
	      return (Typing.varType.get(this.x).type);
	    }
	    else {throw new Error("The variable "+this.x+" does not exist");}
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
	  final String t;
	  Type(String t) {
		  this.t = t;
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
	  abstract void Typer();
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
    @Override
    void Typer(){
      String t= this.e.Typer();
      this.s1.Typer();
      if (s2 != null) {
      this.s2.Typer();
      }
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
    void Typer(){
      String t=this.e.Typer();
      this.s.Typer();
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
    void Typer() {
      String t=this.e.Typer();
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
    void Typer(){
	  for (Decl_variable d:v) {
	        d.TyperLoc();
	  //      System.out.println("ici "+d.x);
	  }
	  // System.out.println(Typing.varTypeLoc);
      for (Stmt s:l) {
        s.Typer();
      }
	 //  System.out.println(Typing.varTypeLoc);
	  for (Decl_variable d:v) {
	        Typing.varTypeLoc.remove(d.x);
	  }


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
	  void Typer() {
		    String t=this.e.Typer();
		  }
	@Override
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables) {	// I send l because it's not a return.
		return this.e.toRTL(l, r, g, variables);
	}
}

abstract /* Declarations */
class Declarations { 
	abstract void Typer();
}

class Decl_variable extends Declarations {
    final String x;
    final LinkedList<String> v;
    final Type t;
    final LinkedList<Var> l;

	Decl_variable(LinkedList<String> x) throws Exception {
	      super();
	      this.x="int";
	      this.v = x;
	      this.t = new Type("int");
	      this.l = new LinkedList<Var>();
	      for (String s : x) {
	        if (Typing.varType.containsKey(s) || Typing.varTypeLoc.containsKey(s)) {
	          throw new Error("This variable name already exists");
	        }
	        this.l.addFirst(new Var(s,"int"));
	      }
	}

	Decl_variable(String x, LinkedList<String> l) throws Exception {
	      super();
	      this.v=l;
	      this.x = x;
	      this.t = new Type(x);
	      this.l = new LinkedList<Var>();

	   /*   if (!Typing.declStruct.containsKey(x)) {
	        throw new Error("This structure does not exist");

	      }*/
	      for (String s:l){
	        //Typing.varType.put(s, new Var(s,"x"));
	        this.l.addFirst(new Var(s,x));
	      }
	}
    @Override
    void Typer() {
      if (this.t.t.equals("struct")) {
        for (Var v:this.l) {
         	/*if (Typing.varType.containsKey(v.name)||Typing.varTypeLoc.containsKey(v.name)) {
    		throw new Error(v.name+" already exists");
    		}*/
          Typing.varType.put(v.name,v);
        }
      }
      else {
        for (Var v:this.l) {
        /*if (Typing.varType.containsKey(v.name)||Typing.varTypeLoc.containsKey(v.name)) {
        		throw new Error(v.name+" already exists");
        	}*/
          Typing.varType.put(v.name,v);
        }
      }

      }
    void TyperLoc() {
        if (this.t.t.equals("struct")) {
          for (Var v:this.l) {
         /*	if (Typing.varType.containsKey(v.name)||Typing.varTypeLoc.containsKey(v.name)) {
      		throw new Error(v.name+" already exists");
      		}*/
            Typing.varTypeLoc.put(v.name,v);
          }
        }
        else {
          for (Var v:this.l) {
         	/*if (Typing.varType.containsKey(v.name)||Typing.varTypeLoc.containsKey(v.name)) {
      		throw new Error(v.name+" already exists");
      		}*/
            Typing.varTypeLoc.put(v.name,v);
          }
        }

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
	final LinkedList<Param> l;

	Decl_struct(String s, LinkedList<Decl_variable> ld) throws Exception {
        super();
        this.s = s;
        this.l = new LinkedList<Param>();
        for (Decl_variable d:ld) {
        	for (String st:d.v) {
        	l.add(new Param(st, d.t.t));
        	}
        }

        HashSet<Param> unique = new HashSet<Param>(l);
        if (unique.size()!=l.size())
        {
          throw new Error("Two fields of the structure have the same name");
        }
        if (Typing.declStruct.containsKey(s)) {
          throw new Error("This structure name already exists");
        }
	}
    @Override
    void Typer() {
        if (Typing.declStruct.containsKey(s)) {
            throw new Error("This structure name already exists");
          }
      Typing.declStruct.put(this.s,this.l);
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
    @Override
    void Typer(){
      if (Typing.varType.containsKey(this.f)) {
        throw new Error("This structure name already exists");
      }
      //System.out.println("Typing the function " +this.f);
      Typing.varType.put(f, new Var(f,r.t));
      LinkedList<Var> typeArgs = new LinkedList<Var>();
      for (Param p:this.l) {
        typeArgs.addLast(new Var(p.v.x,p.t.t));
        Typing.varTypeLoc.put(p.v.x,new Var(p.v.x,p.t.t));
      }
     // System.out.println("varTypeLoc : "+Typing.varTypeLoc);
      //System.out.println("varType : "+Typing.varType);
      Typing.funArgsType.put(f,typeArgs);
      this.b.Typer();
      for (Param p:this.l) {
        Typing.varTypeLoc.remove(p.v.x);
      }

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
    String Typer(){ //TODO, the sizeof is not complete is the parser.cup
	      if (Typing.declStruct.containsKey(s)) {
	      return("int");
	      }
	      else {throw new Error("This structure does not exist");}
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
    public Param(String v, String s) throws Exception {
      this.v = new Evar(v);
      this.t = new Type(s);
    }
    public Param(Evar v) throws Exception {
      this.v = v;
      this.t = new Type("int");
    }
    public Param(Evar v, String s) throws Exception {
        this.v = v;
        this.t = new Type(s);
      }
    public Param(String x) throws Exception {
        this.v = new Evar(x);
        this.t = new Type("int");
      }
	void initializeVar(List<Register> formals, Map<String, Register> variables) { // Initialize variables
		Register r = new Register();
		variables.put(v.getX(), r);
		formals.add(r);
	}
   /* public Param(Decl_variable d) {
    	for (String s:d.v) {
    	Param
    	}
    }*/

  }

/* File */
class File {
	final LinkedList<Declarations> l;

	File(LinkedList<Declarations> l) {
		super();
		this.l = l;
	}
    void Typer() {
    	Typing T=new Typing();
      for (Declarations d:l) {
        d.Typer();
       // System.out.println("declStruct : "+Typing.declStruct);
       // System.out.println("varType : "+Typing.varType);
        //System.out.println("varTypeLoc : "+Typing.varTypeLoc);
      }
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

class Var {
    String name;
    String type;
    Var(String n, String t) {
      this.name=n;
      this.type=t;
    }
  }

