package mini_c;


import java.util.LinkedList;
import java.util.*;
import java.util.HashMap;
import java.util.HashSet;

//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import javax.crypto.spec.DESedeKeySpec;
import java.util.LinkedList;
import java.util.StringJoiner;

/* Syntaxe abstraite de Mini-Python */

/* opérateurs unaires et binaires */

enum Unop { Uneg, Unot }

enum Binop {
  Badd , Bsub , Bmul , Bdiv ,
  Beqeq , Bneq , Blt , Ble , Bgt , Bge, // comparaison structurelle
  Band , Bor, Beq, Bobj // paresseux
}

/* constantes litérales */


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


class Constant {
  int c;
  public Constant(int c) {
    super();
    this.c = c;
  }
  String Typer() {
    return("int");
  }
}

/* expressions */

abstract class Expr {
  abstract String Typer();
}
class Ecst extends Expr { // Integer
  final Constant c;
  Ecst(Constant c) {
    this.c = c;
  }
  @Override
  String Typer() {
    return("int");
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
              if (x2.equals(p.v)) {
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
        if (t1.equals("int") && t2.equals("int")) {
          return ("int");
        }
        else {throw new Error("Bad type, operation impossible");}
      case Beq:
    	  t2 = this.e2.Typer();
          if (this.e1 instanceof Evar) {
        	  
            if (Typing.equalsType(t1,t2)) {
              return t1;
            }
            else {throw new Error("invalid type type left is "+t1+" and type right is "+t2);}
          }
          else if (this.e1 instanceof Ebinop) {
            switch(((Ebinop) this.e1).op){
              case Bobj:
            	  System.out.println("Le type 1 est "+t1+" et le type 2 est "+t2);
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
  String Typer() {
    if (Typing.varType.containsKey(this.f)) {
      LinkedList<Var> typeArgs= Typing.funArgsType.get(this.f);
      Iterator<Var> it= typeArgs.iterator();
      if (typeArgs.size()!=this.l.size()) {
    	  throw new Error("invalid number of arguments");
      }
      for (Expr e:this.l) {
        if (!e.Typer().equals(it.next().type)) {
          throw new Error("invalid argument type");
        }
      }
      return (Typing.varType.get(this.f).type);
    }
    else {
      throw new Error("This function does not exist");
    }
  }
}
class Evar extends Expr {
  final String x;
  public Evar(String x) {
    this.x = x;
  }
  @Override
  String Typer() {
    if (Typing.varType.get(this.x)!= null) {
      return (Typing.varType.get(this.x).type);
    }
    else if (Typing.varTypeLoc.get(this.x)!=null) {
      return (Typing.varTypeLoc.get(this.x).type);
    }
    else {throw new Error("The variable "+this.x+" does not exist");}
  }
}

//class Type {
//  final String t;
//  Type(String t) throws Exception {
//    if ((t == "int") || (t == "struct") ) { this.t = t; }
//    else { throw new Exception("Type incorrect"); }
//  }
//}
class Type {
	  final String t;
	  Type(String t) {
		  this.t = t;
	  }
	}

/* instruction */
abstract class Stmt {
  abstract void Typer();
}

  class Sif extends Stmt {
    final Expr e;
    final Stmt s1, s2;
    Sif(Expr e, Stmt s) {
      super();
      this.e = e;
      this.s1 = s;
      this.s2 = new Sblock(new LinkedList<Stmt>(),new LinkedList<Decl_variable>());
    }
    Sif(Expr e, Stmt s1, Stmt s2) {
      super();
      this.e = e;
      this.s1 = s1;
      this.s2 = s2;
    }
    @Override
    void Typer(){
      String t= this.e.Typer();
      this.s1.Typer();
      this.s2.Typer();
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
  }

  class Sblock extends Stmt {
  	final LinkedList<Stmt> l;
  	final LinkedList<Decl_variable> v;
  	Sblock(LinkedList<Stmt> l, LinkedList<Decl_variable> v) {
  		super();
  		this.l = l;
  		this.v = v;
  	}
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
  void Typer() {
    String t=this.e.Typer();
  }
}
/* Declarations */
abstract class Declarations {
  abstract void Typer();
}
  class Decl_variable extends Declarations {
    final String x;
    final LinkedList<String> v;
    final Type t;
    final LinkedList<Var> l;
    // Decl_variable(String x) {
    //   this.x=x;
    //   this v= new LinkedList<String>();
    //   this.t = new Type("int");
    //   this.l = new LinkedList<String>();
    //   if (Typing.varType.containsKey(x)) {
    //     throw new Error("This variable name already exists");
    //   }
    //   Typing.varType.put(x,"int");
    // }

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
        //Typing.varType.put(s,new Var(s,"int"));
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
          Typing.varType.put(v.name,v);
        }
      }
      else {
        for (Var v:this.l) {
          Typing.varType.put(v.name,v);
        }
      }

      }
    void TyperLoc() {
        if (this.t.t.equals("struct")) {
          for (Var v:this.l) {
            Typing.varTypeLoc.put(v.name,v);
          }
        }
        else {
          for (Var v:this.l) {
            Typing.varTypeLoc.put(v.name,v);
          }
        }

        }
    }

  class Decl_struct extends Declarations {
    final String s;
    final LinkedList<Param> l;
  /*  Decl_struct(String s, LinkedList<Param> l) {
      super();
      this.s = s;
      this.l = l;
      HashSet<Param> unique = new HashSet<Param>(l);
      if (unique.size()!=l.size())
      {
        throw new Error("Two fields of the structure have the same name");
      }
      if (Typing.declStruct.containsKey(s)) {
        throw new Error("This structure name already exists");
      }
      //Typing.declStruct.put(s,l);
    }*/
    
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
      Typing.declStruct.put(this.s,this.l);
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
		
	    @Override
	    void Typer(){
	    	System.out.println("Typing the function " +this.f);
	      Typing.varType.put(f, new Var(f,r.t));	
	      LinkedList<Var> typeArgs = new LinkedList<Var>();
	      for (Param p:this.l) {
	        typeArgs.addLast(new Var(p.v,p.t.t));
	        Typing.varTypeLoc.put(p.v,new Var(p.v,p.t.t));
	      }
	      System.out.println("varTypeLoc : "+Typing.varTypeLoc);
	      System.out.println("varType : "+Typing.varType);
	      Typing.funArgsType.put(f,typeArgs);
	      this.b.Typer();
	      for (Param p:this.l) {
	        Typing.varTypeLoc.remove(p.v);
	      }

	    }

	}
/*  class Decl_function extends Declarations { 			// Declaration of a function
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
  */
  class Sizeof extends Expr {
    final String s;
    public Sizeof(String s) {
      this.s = s;
    }
    String Typer(){ //TODO, the sizeof is not complete is the parser.cup
      if (Typing.declStruct.containsKey(s)) {
      return("int");
      }
      else {throw new Error("This structure does not exist");}
    }
  }

class Param {
    final String v;
    final Type t;
    public Param(String v, String s) throws Exception {
      this.v = v;
      this.t = new Type(s);
    }
    public Param(Evar v) throws Exception {
      this.v = v.x;
      this.t = new Type("int");
    }
    public Param(Evar v, String s) throws Exception {
        this.v = v.x;
        this.t = new Type(s);
      }
    public Param(String x) throws Exception {
        this.v = x;
        this.t = new Type("int");
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
  }

class Var {
    String name;
    String type;
    Var(String n, String t) {
      this.name=n;
      this.type=t;
    }
  }
