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
  public static HashMap<String,LinkedList<Var>> declStruct = new HashMap<String,LinkedList<Param>>(); // there we store the list of pointers a structure contains
  public static HashMap<String,Var> varType = new HashMap<String, Var>(); //there we store the variable declared as int, the value doesn't serve anything
  //HashMap<String,String> varsPoints = new HashMap<String, String>(); //there we store the struct id * vars, the key is the name of the pointer, the value is the id
  public static HashMap<String,LinkedList<Var>> funArgsType=new HashMap<String,LinkedList<Var>>(); //we store, for each function the list of the type of its arguments
  //HashMap<String,String> funType=new HashMap<String,String>();
  public static HashMap<String,Var> varTypeLoc = new HashMap<String, Var>();
  Typing() {

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
    String t2 = this.e2.Typer();
    switch (this.op) {
      case Bobj:
      if (this.e1 instanceof Evar) {
        if (this.e2 instanceof Evar) {
          if (Typing.declStruct.containsKey(this.e1)) {
            //int n = Typing.declStruct.get(((Evar)this.e1).x).indexOf();
            for (Decl_variable v :Typing.declStruct.get(((Evar)this.e1).x)) {
              if (true) {
                return Typing.varType.get(((Evar)this.e2).x);
              }
            }
            throw new Error("This field does not exist");
          }
          else {
            throw new Error("This structure does no exist");

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
        if (this.e1.Typer() == this.e2.Typer()) {
          return (this.e1.Typer());
        }
        else {throw new Error("Bad type expression");}
      case Bneq :
        if (this.e1.Typer() == this.e2.Typer()) {
          return ("int");
        }
        else {throw new Error("Bad type expression");}
      case Blt:
        if (this.e1.Typer() == this.e2.Typer()) {
          return ("int");
        }
        else {throw new Error("Bad type expression");}
      case Bgt:
        if (this.e1.Typer() == this.e2.Typer()) {
          return ("int");
        }
        else {throw new Error("Bad type expression");}
      case Ble:
        if (this.e1.Typer() == this.e2.Typer()) {
          return ("int");
        }
        else {throw new Error("Bad type expression");}
      case Bge:
        if (this.e1.Typer() == this.e2.Typer()) {
          return ("int");
        }
        else {throw new Error("Bad type expression");}
      case Bor:
        String s1 = this.e1.Typer();
        String s2 = this.e2.Typer();
        return("int");
      case Band:
        String ss1 = this.e1.Typer();
        String ss2 = this.e2.Typer();
        return("int");
      case Badd:
        if (t1.equals("int") && t2.equals("int")) {
          return ("int");
        }
        else {throw new Error("Bad type, impossible to add");}
      case Bmul:
        if (this.e1.Typer()== "int" && this.e2.Typer() == "int") {
          return ("int");
        }
        else {throw new Error("Bad type, impossible to add");}
      case Bsub:
        if (this.e1.Typer()== "int" && this.e2.Typer() == "int") {
          return ("int");
        }
        else {throw new Error("Bad type, impossible to add");}
      case Bdiv:
        if (this.e1.Typer()== "int" && this.e2.Typer() == "int") {
          return ("int");
        }
        else {throw new Error("Bad type, impossible to add");}
      case Beq:
          if (this.e1 instanceof Evar) {
            if (t1 == t2) {
              return t1;
            }
            else {throw new Error("invalid type");}
          }
          else if (this.e1 instanceof Ebinop) {
            switch(this.op){
              case Bobj:
              if (t1 == t2) {
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
      Iterator<String> it= new Typing.funArgsType.get(this.f).iterator();
      for (Expr e:this.l) {
        if (e.Typer()!=it.next()) {
          throw new Error("invalid argument type");
        }
      }
      return (Typing.varType.get(this.f));
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
      return (Typing.varType.get(this.x));
    }
    else {throw new Error("This variable does not exist");}
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
abstract class Stmt {
  abstract void Typer();
}

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
    @Override
    void Typer(){
      String t= this.e.Typer();
      this.s1.Typer();
      this.s2.Typer();
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
  /*class Sblock extends Stmt {
  final LinkedList<Stmt> l;
  final LinkedList<Decl_variable> v;
  Sblock() {
  this.v = new LinkedList<Decl_variable>();
  this.l = new LinkedList<Stmt>();
}
Sblock(LinkedList<Stmt> l, LinkedList<Decl_variable> v) {
super();
this.l = l;
this.v = v;
}
}*/
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
class Declarations {}
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
      this.v = x;
      this.t = new Type("int");
      this.l = new LinkedList<Var>();
      for (String s : x) {
        if (Typing.varType.containsKey(s)) {
          throw new Error("This variable name already exists");
        }
        Typing.varType.put(s,new Var(s,"int"));
      }
    }
    Decl_variable(String x, LinkedList<String> l) throws Exception {
      super();
      this.x = x;
      this.t = new Type("struct");
      this.l = l;

      if (!Typing.declStruct.containsKey(x)) {
          throw new Error("This structure does not exist");

      }
      for (String s:l){
        Typing.varType.put(s, new Var(s,"x"));
    }
  }
  class Decl_struct extends Declarations {
    final String s;
    final LinkedList<Decl_variable> l;
    Decl_struct(String s, LinkedList<Param> l) {
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
      Typing.declStruct.put(s,l);
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
      LinkedList<String> typeArgs = new LinkedList<String>();
      for (Param p:this.l) {
        typeArgs.addLast(p.t.t);
        Typing.varTypeLoc.put(p.v.x,new Var(p.v.x,p.t.t));
      }
      Typing.funArgsType.put(f,typeArgs);
      for (Stmt stmt:s) {
        stmt.Typer();
      }
      for (Param p:this.l) {
        Typing.varTypeLoc.remove(p.v.x);
      }
    }


  }
  class Sizeof {
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

class Var {
    String name;
    String type;
    Var(String n, String t) {
      this.name=n;
      this.type=t;
    }
  }
