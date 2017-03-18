package mini_c;

import java.util.*;

import static mini_c.Binop.*;
import static mini_c.Mbbranch.*;
import static mini_c.Mbinop.*;

/** README:
* 		The last parameters given to the function with structures and those things are because of a bad communication between
 * 	the typer and the RTL. We had no time to change it but I recognize is horrible. TODO!
* */

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
	// TODO: toRTL can use Typer to make it less parameters! how didn't I see that before! I hate me.
	/*
	* 	struct_definition : map String - list of strings.
	* 		Contains the name of the struct (first string) and the list of all the names inside the struct.
	* 		Ex: struct S { int a; int b; } the a and b will be the list
	*	struct_declaration: contains the name of the pointer of a structure, and which sturcture it is.
	*		Ex: struct S *p; maps p with S.
	* */
	abstract Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration);
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
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
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
	  @Override String Typer() {
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
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		/* Many cases in this big switch were make to optimize the compiler. It seems bigger but it's only to make it more efficient, making the compiler work when there are constants */
		switch (this.op) {
			case Beq: // x = y
				/* 2 option for x, or a variable or a p->a, else is not a lvalue */
				Label L2;
				if (e1 instanceof Evar) { // 1st option, x is a variable
					if (variables.containsKey(((Evar) e1).getX())) {	// x can be a local variable
						Rmbinop rmbinop = new Rmbinop(Mmov, r, variables.get(((Evar) e1).getX()), l);
						L2 = g.add(rmbinop);
					}
					else {	// x is a global variable
						Rassign_global rag = new Rassign_global(r, ((Evar) e1).getX(), l);
						L2 = g.add(rag);
					}
				}
				else if (e1 instanceof Ebinop) {	// 2nd option, p->x = y;
					if (variables.containsKey(((Evar)((Ebinop)e1).e1).getX())) { // Local Variable
						if (((Ebinop) e1).op != Bobj) {
							throw new Error(e1.toString() + " not lValue");
						}
						//Register r2 = new Register();
						/* Get the index of the pointer (a) */
						LinkedList<String> as = struct_definition.get(struct_declaration.get(((Evar) ((Ebinop) e1).e1).getX()));
						if ((as == null) || (as.isEmpty())) { throw new Error(((Evar) ((Ebinop) e1).e1).getX() + " of " + struct_declaration.get(((Evar) ((Ebinop) e1).e1).getX()) + " struct was either empty or not defined. The typer should have got this."); }
						int a = as.indexOf(((Evar) ((Ebinop) e1).e2).getX());
						if (a == -1) { throw new Error("Structure " + ((Evar) ((Ebinop) e1).e1).getX() + " has no element " + ((Evar) ((Ebinop) e1).e2).getX() + ". The typer should have got this."); }

						Rstore rstore = new Rstore(r, variables.get(((Evar) ((Ebinop) e1).e1).getX()), a, l); // r2 = p
						/*Label L3 */
						L2 = g.add(rstore);
						//L2 = ((Ebinop) e1).getE1().toRTL(L3, variables.get(((Evar)((Ebinop)e1).e1).getX()), g, variables, struct_definition, struct_declaration);
					}
					else {
						throw new Error("global structure not yet implemented");	// TODO
					}
				}
				else { throw new Error(e1.toString() + " not lValue"); }
				Label L1 = this.e2.toRTL(L2, r, g, variables, struct_definition, struct_declaration);
				return L1;
			case Bobj: // p->a (used to load only)
				Register r2 = new Register();
				/* Get the index of the pointer */
				LinkedList<String> as = struct_definition.get( struct_declaration.get(((Evar) e1).getX()));
				if ( (as == null) || (as.isEmpty())) { throw new Error( ((Evar) e1).getX() + " of " + struct_declaration.get(((Evar) e1).getX()) + " struct was either empty or not defined. The typer should have got this."); }
				int a = as.indexOf(((Evar) e2).getX());
				if (a == -1) { throw new Error("Structure " + ((Evar) e1).getX() + " has no element " + ((Evar) e2).getX() + ". The typer should have got this."); }

				Rload rs = new Rload(variables.get(((Evar) e1).getX()), r, a,  l); // r = p, r2 = new register and i = a (offset)
				return g.add(rs);
				//Label L3 = e1.toRTL(L4, r2, g, variables, struct_definition, struct_declaration);
				//return L3;
				// TODO: implement the global
		}
		if ( (e2 instanceof Ecst) && (e1 instanceof Ecst)) { // 5 op 4 (Only constants) ALL THIS IF IS TO MAKE COMPILER TAKE OUT SOME JOB. IT'S NOT NECESARRY NORMALLY
			switch (this.op) { // in this case, the compilator will transform things like 4 + 5 directly to 9 in compilation time to make it more effective!
				case Badd:
					return new Ecst(new Constant(((Ecst) e1).getInt() + ((Ecst) e2).getInt())).toRTL(l, r, g, variables, struct_definition, struct_declaration);
				case Bsub:
					return new Ecst(new Constant(((Ecst) e1).getInt() - ((Ecst) e2).getInt())).toRTL(l, r, g, variables, struct_definition, struct_declaration);
				case Bmul:
					return new Ecst(new Constant(((Ecst) e1).getInt() * ((Ecst) e2).getInt())).toRTL(l, r, g, variables, struct_definition, struct_declaration);
				case Bdiv:
					return new Ecst(new Constant(((Ecst) e1).getInt() / ((Ecst) e2).getInt())).toRTL(l, r, g, variables, struct_definition, struct_declaration);
				case Bmod:
					return new Ecst(new Constant(((Ecst) e1).getInt() % ((Ecst) e2).getInt())).toRTL(l, r, g, variables, struct_definition, struct_declaration);
				case Beqeq:
					if (((Ecst) e1).getInt() == ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					}
				case Band:
					if ( ((Ecst) e1).getInt() != 0 && ((Ecst) e2).getInt() != 0 ) { return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration); }
					else { return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration); }
				case Bor:
					if ( ((Ecst) e1).getInt() == 0 && ((Ecst) e2).getInt() == 0 ) { return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration); }
					else { return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration); }
				case Beq:
					throw new Error("Constant = Constant not available");
				case Bneq:
					if (((Ecst) e1).getInt() != ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					}
				case Blt:
					if (((Ecst) e1).getInt() < ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					}
				case Ble:
					if (((Ecst) e1).getInt() <= ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					}
				case Bgt:
					if (((Ecst) e1).getInt() > ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					}
				case Bge:
					if (((Ecst) e1).getInt() >= ((Ecst) e2).getInt()) {
						return new Ecst(new Constant(1)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					} else {
						return new Ecst(new Constant(0)).toRTL(l, r, g, variables, struct_definition, struct_declaration);
					}
				default:
					throw new Error("Incompatible operation between integers");
			}
		}
		else if ( (e1 instanceof Ecst) || (e2 instanceof Ecst) ) { //If i reach here then e1 && e2 are not both constants
			if ((this.op == Badd)) { // this is x + 4 or 4 + x
				if (e1 instanceof Ecst) {	// I add the constant directly as add $4 x instead of charging it to a register
					Rmunop rmunop = new Rmunop(new Maddi(((Ecst) e1).getInt()), r, l);
					Label L2 = g.add(rmunop);
					Label L1 = this.e2.toRTL(L2, r, g, variables, struct_definition, struct_declaration);
					return L1;
				}
				else {
					Rmunop rmunop = new Rmunop(new Maddi(((Ecst) e2).getInt()), r, l);
					Label L2 = g.add(rmunop);
					Label L1 = this.e1.toRTL(L2, r, g, variables, struct_definition, struct_declaration);
					return L1;
				}
			}
			else if ((this.op == Bsub) && (e2 instanceof Ecst)) { // this is x - 4 or 4 - x. Same as above but for subs
				Rmunop rmunop = new Rmunop(new Maddi(-((Ecst) e2).getInt()), r, l);
				Label L2 = g.add(rmunop);
				Label L1 = this.e1.toRTL(L2, r, g, variables, struct_definition, struct_declaration);
				return L1;
			}
		}
		/* If I reached here then they are "normal" operations (+, /, *, -, <, >, etc ) and no 2 constants */
		Register r2 = new Register();
		//Rmbinop rb;
		Label L3;
		Rmbinop rb;
		Rmbbranch rmb;
		Rmubranch rmu;
		Label lt, lf;
		Rconst cst;
		switch (this.op) {
			// This is for cases like 1 && 3 where I should give 1 as a result so I check the Beqeq and things like that.
			// The default cases are where I had thought I had normal binop operations (+, *, /, -)
			case Bsub:
				rb = new Rmbinop(Binop2Mbinop(), r, r2, l);
				L3 = g.add(rb);
				break;
			case Beqeq:
				cst = new Rconst(1, r, l);
				lt = g.add(cst);
				cst = new Rconst(0, r, l);
				lf = g.add(cst);
				rmb= new Rmbbranch(Mjeqeq, r2, r, lt, lf);
				L3 = g.add(rmb);
				break;
			case Bneq:
				cst = new Rconst(1, r, l);
				lt = g.add(cst);
				cst = new Rconst(0, r, l);
				lf = g.add(cst);
				rmb= new Rmbbranch(Mjneq, r2, r, lt, lf);
				L3 = g.add(rmb);
				break;
			case Blt:
				cst = new Rconst(1, r, l);
				lt = g.add(cst);
				cst = new Rconst(0, r, l);
				lf = g.add(cst);
				rmb= new Rmbbranch(Mjl, r2, r, lt, lf);
				L3 = g.add(rmb);
				break;
			case Ble:
				cst = new Rconst(1, r, l);
				lt = g.add(cst);
				cst = new Rconst(0, r, l);
				lf = g.add(cst);
				rmb= new Rmbbranch(Mjle, r2, r, lt, lf);
				L3 = g.add(rmb);
				break;
			case Bgt:
				cst = new Rconst(1, r, l);
				lt = g.add(cst);
				cst = new Rconst(0, r, l);
				lf = g.add(cst);
				rmb= new Rmbbranch(Mjg, r2, r, lt, lf);
				L3 = g.add(rmb);
				break;
			case Bge:
				cst = new Rconst(1, r, l);
				lt = g.add(cst);
				cst = new Rconst(0, r, l);
				lf = g.add(cst);
				rmb= new Rmbbranch(Mjge, r2, r, lt, lf);
				L3 = g.add(rmb);
				break;
			case Beq:
				throw new Error("Don't know how I get here");
			case Bobj:
				throw new Error("Don't know how I get here");
			case Bmod:
				throw new Error("Don't know how I get here");
			default:
				rb = new Rmbinop(Binop2Mbinop(), r2, r, l);
				L3 = g.add(rb);
				break;
		}
		Label L2 = this.e1.toRTL(L3, r2, g, variables, struct_definition, struct_declaration);
		Label L1 = this.e2.toRTL(L2, r, g, variables, struct_definition, struct_declaration);
		return L1;
	}

	private Mbinop Binop2Mbinop() {
		switch (this.op) {
			case Badd: return Mbinop.Madd;
			case Bsub: return Mbinop.Msub;
			case Bdiv: return Mbinop.Mdiv;
			case Bmul: return Mbinop.Mmul;
			case Band: return Mbinop.Mand;
			case Bor: return Mbinop.Mor;
			/* This cases are made differently. And checked before getting here (Normally once inside this function this cannot happen) */
			//case Beqeq: return Mbinop.Msete;
			//case Bneq: return Msetne;
			//case Blt: return Msetl;
			//case Ble: return Msetle;
			//case Bgt: return Mbinop.Msetg;
			//case Bge: return Mbinop.Msetge;
			//case Bmod: return Mbinop.Mmov;
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
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		switch(this.op) {
			case Uneg:
				if (this.e instanceof Ecst) {	/* if e is a cst just compute -e in compilation time */
					Ecst ecst = new Ecst( new Constant( -((Ecst) this.e).getInt()));
					return ecst.toRTL(l, r, g, variables, struct_definition, struct_declaration);
				}
				else {	/* The -a is done by making "0 - a" */
					Ecst c = new Ecst(new Constant(0));
					Register r2 = new Register();
					Rmbinop rb = new Rmbinop(Mbinop.Msub, r2, r, l);
					Label L3 = g.add(rb);
					Label L2 = this.e.toRTL(L3, r2, g, variables, struct_definition, struct_declaration);
					Label L1 = c.toRTL(L2, r, g, variables, struct_definition, struct_declaration);
					return L1;
				}
			case Unot: /* if(true) return 0; else return 1; */
				//Not so fancy (nor effective) but it works...
				Sif sif = new Sif(this.e, new Seval(new Ecst(new Constant(0))), new Seval(new Ecst(new Constant(1))));
				return sif.toRTL(l, l, r, g, variables, struct_definition, struct_declaration);
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
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		LinkedList<Register> rl = new LinkedList<>();
		Rcall rc = new Rcall(r, this.f, rl, l);
		Label L = g.add(rc);
		for ( Expr expr : this.l) { // Save all the variables into registers
			r = new Register();
			L = expr.toRTL(L, r, g, variables, struct_definition, struct_declaration);
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
	  @Override String Typer() {
	    if (Typing.varTypeLoc.get(this.x)!= null) {
	      return (Typing.varTypeLoc.get(this.x).type);
	    }
	    else if (Typing.varType.get(this.x)!=null) {
	      return (Typing.varType.get(this.x).type);
	    }
	    else {throw new Error("The variable "+this.x+" does not exist");}
	  }

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {// Here the variable is already created
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
	abstract Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration);
	/* RTLc as made in the slides for branches */
	Label toRTLc(Expr ex, Label truel, Label falsel, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		// TODO: see if I have a constant and do the simpler jumps
		if (ex instanceof Ebinop) { // two parts in the branch expression (ex. d < 4)
			switch(((Ebinop) ex).getOp()) {
				case Beqeq:	// a == 0
					return evaluate( Mjeqeq, truel, falsel, r, g, (Ebinop)ex, variables, struct_definition, struct_declaration);
				case Bneq: 	// a != 0
					return evaluate( Mjneq, truel, falsel, r, g, (Ebinop)ex, variables, struct_definition, struct_declaration);
				case Blt:	// a < 0
					return evaluate( Mbbranch.Mjl, truel, falsel, r, g, (Ebinop)ex, variables, struct_definition, struct_declaration);
				case Ble:	// a <= 0
					return evaluate( Mbbranch.Mjle, truel, falsel, r, g, (Ebinop)ex, variables, struct_definition, struct_declaration);
				case Bgt:	// a > 0
					return evaluate( Mbbranch.Mjg, truel, falsel, r, g, (Ebinop)ex, variables, struct_definition, struct_declaration);
				case Bge:	// a >= 0
					return evaluate( Mbbranch.Mjge, truel, falsel, r, g, (Ebinop)ex, variables, struct_definition, struct_declaration);
				case Band:	// a && 0
					return evaluate(new Mjz(), toRTLc(((Ebinop)ex).getE2(), truel, falsel, r, g, variables, struct_definition, struct_declaration), falsel, r, g, ((Ebinop)ex).getE1(), variables, struct_definition, struct_declaration);
				case Bor:	// a || 0
					return evaluate(new Mjz(), truel, toRTLc(((Ebinop)ex).getE2(), truel, falsel, r, g, variables, struct_definition, struct_declaration), r, g, ((Ebinop)ex).getE1(), variables, struct_definition, struct_declaration);
			}
		}	// If it's just a normal statement do the jz.
		if (ex instanceof Eunop) {
			switch(((Eunop) ex).getOp()) { // Kept in case I have to implement the Uneg in the future.
				case Unot: return evaluate( new Mjz(), falsel, truel, r, g, ex, variables, struct_definition, struct_declaration); // Give them in the other sense because I will count the e part of !e.
			}
		}
		return evaluate( new Mjz(), truel, falsel, r, g, ex, variables, struct_definition, struct_declaration);
	}
	private Label evaluate(Mbbranch m, Label truel, Label falsel, Register r, RTLgraph g, Ebinop e /* Only used for binoms */, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		Register r1 = r; // I know is redundant but I see it more clear... Hope the compiler is intelligent right?
		Register r2 = new Register();
		Rmbbranch rb = new Rmbbranch(m, r1, r2, truel, falsel);
		Label L3 = g.add(rb);
		Label L2 = e.getE2().toRTL(L3, r2, g, variables, struct_definition, struct_declaration);
		Label L1 = e.getE1().toRTL(L2, r1, g, variables, struct_definition, struct_declaration);
		return L1;
	}
	private Label evaluate(Mubranch m, Label truel, Label falsel, Register r, RTLgraph g, Expr re, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		// TODO: Because of the structure of machines. This may not be effective. Maybe it's better to use jnz
		Rmubranch rb = new Rmubranch(m, r, falsel, truel); // Turn around the true and false because it will be a jz (so in case it is zero I should do the false one)
		Label L2 = g.add(rb);
		if (re instanceof Eunop) {
			if (((Eunop) re).getOp() == Unop.Unot) {
				Label L1 = ((Eunop) re).getE().toRTL(L2, r, g, variables, struct_definition, struct_declaration);
				return L1;
			}
		}
		Label L1 = re.toRTL(L2, r, g, variables, struct_definition, struct_declaration); // This should not be called in the !re case.
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		Label truel = s1.toRTL(l, ret, new Register(), g, variables, struct_definition, struct_declaration);
		Label falsel = l;
		if (s2 != null) { falsel = s2.toRTL(l, ret, new Register(), g, variables, struct_definition, struct_declaration); } // It will be null if I don't have the "else" statement.
		return toRTLc(this.e, truel, falsel, r, g, variables, struct_definition, struct_declaration);
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		Label L = new Label();
		Label loop = this.s.toRTL(L, ret, new Register(), g, variables, struct_definition, struct_declaration);
		Label Le = toRTLc(e, loop, l, r, g, variables, struct_definition, struct_declaration);
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		return this.e.toRTL(ret, r, g, variables, struct_definition, struct_declaration); // I give ret (f.exit) because the return exit the function
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		Stmt s = null;
		do{
			try { s = this.l.removeLast(); }
			catch (NoSuchElementException e) { return l; }
			l = s.toRTL(l, ret, r, g, variables, struct_definition, struct_declaration);
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
	Label toRTL(Label l, Label ret, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {	// I send l because it's not a return.
		return this.e.toRTL(l, r, g, variables, struct_definition, struct_declaration);
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
	void initializeVar(Set<Register> locals, Map<String, Register> variables, Map<String, String> struct_map) { // Initialize variables
		for ( String s : v ) {
			Register r = new Register();
			variables.put(s, r);
			locals.add(r);
			struct_map.put(s, this.x);
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
    @Override void Typer(){
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
	RTLfun toRTL(Map<String, LinkedList<String>> sd) {
		RTLfun f = new RTLfun(this.f);
		LinkedList<Stmt> lstStmt = this.b.l; // List of statements
		LinkedList<Param> lstParam = this.l; // List of input parameters
		LinkedList<Decl_variable> lstVar = this.b.v; // List of declaration of variables
		//f.struct_definition = sd;
		f.body = new RTLgraph();
		f.exit = new Label();
		Label current = f.exit;
		/* Declaration of local Variables */
		for ( Param p: lstParam) {
			p.initializeVar(f.formals, f.variables);
		}
		for (Decl_variable dv : lstVar) {
			dv.initializeVar(f.locals, f.variables, f.struct_declarations);
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
			current = s.toRTL(current, f.exit, f.result, f.body, f.variables, sd, f.struct_declarations);
		} while(true);
	}
}

class Sizeof extends Expr {
	final private String s;

	Sizeof(String s) {
		this.s = s;
	}
	@Override
    String Typer() {
		//TODO, the sizeof is not complete is the parser.cup
		if (Typing.declStruct.containsKey(s)) { return("int"); }
		else {throw new Error("This structure does not exist");}
	    }

	@Override
	Label toRTL(Label l, Register r, RTLgraph g, Map<String, Register> variables, Map<String, LinkedList<String>> struct_definition, Map<String, String> struct_declaration) {
		LinkedList<Expr> structSized = new LinkedList<>();
		structSized.add(new Evar(this.s));
		Ecall callSizeOf = new Ecall("sizeof", structSized);
		return callSizeOf.toRTL(l, r, g, variables, struct_definition, struct_declaration);
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
		int size = formals.size();
		Register r = new Register();
		//if (size < parameters.size()) { r = parameters.get(size); }
		//else { r = new Register(); }
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
    	Typing T = new Typing();
      for (Declarations d:l) {
        d.Typer();
       // System.out.println("declStruct : "+Typing.declStruct);
       // System.out.println("varType : "+Typing.varType);
        //System.out.println("varTypeLoc : "+Typing.varTypeLoc);
      }
    }

	RTLfile toRTL() {
		RTLfile file = new RTLfile();
		boolean printGlobal = true;
		for (Declarations d : this.l) {
			if (d instanceof Decl_variable) {
				if (printGlobal && !((Decl_variable) d).v.isEmpty()) {
					System.out.println("Global Variables: ");
					printGlobal = false;
				}
				for ( String s : ((Decl_variable) d).v ) {
					file.gvars.add(s);
					System.out.println("int " + s);
				}
			}
			else if (d instanceof Decl_struct) {
				LinkedList<String> li = new LinkedList<>();
				for (Param p : ((Decl_struct) d).l) {
					li.add(p.v.getX());
				}
				file.struct_definition.put(((Decl_struct) d).s, li);
				if (printGlobal) {
					System.out.println("Global Variables: ");
					printGlobal = false;
				}
				System.out.println("struct " + ((Decl_struct) d).s );
				for (String string : file.struct_definition.get(((Decl_struct) d).s)) {
					System.out.println("\tint " + string);	// TODO: not necessarry a int
				}
			}
			else if (d instanceof Decl_function) {
				RTLfun f = ((Decl_function) d).toRTL(file.struct_definition);
				f.print();
				file.funs.add(f);
			}
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