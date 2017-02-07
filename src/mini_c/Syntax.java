package mini_c;

import java.util.LinkedList;
import java.util.*;
import java.util.HashMap;
import java.util.HashSet;

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
  static HashMap<String,LinkedList<Decl_variable>> declStruct = new HashMap<String,LinkedList<Decl_variable>>(); // there we store the list of pointers a structure contains
  static HashMap<String,String> varType = new HashMap<String, String>(); //there we store the variable declared as int, the value doesn't serve anything
  //HashMap<String,String> varsPoints = new HashMap<String, String>(); //there we store the struct id * vars, the key is the name of the pointer, the value is the id
  static HashMap<String,LinkedList<String>> funArgsType=new HashMap<String,LinkedList<String>>(); //we store, for each function the list of the type of its arguments
  //HashMap<String,String> funType=new HashMap<String,String>();
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

  String Typer();
}
class Ecst extends Expr { // Integer
  final Constant c;
  Ecst(Constant c) {
    this.c = c;
  }
  void Typer() {
    constType.put(c,"int");
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
  String Typer() {
    switch (this.op) {
      case Bobj:
        switch (this.e1) {
        case Evar:
        switch(this.e2) {
          case Evar:
          if (Typing.declStruct.containsKey(this.e1)) {
            for (Decl_var v :Typing.declStruct.get(this.e1)) {
              if (v.x == this.e2.x) {
                return Typing.varType.get(this.e2.x);
              }
            }

              throw new Error("This field does not exist");
          }
          else {
            throw new Error("This structure does no exist");
          }

          default:
          throw new Error("Not a field");
        }
        default:
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
        if (this.e1.Typer()== "int" && this.e2.Typer() == "int") {
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
        switch(this.e1) {
          case Evar:
            if (this.e1.Typer() == this.e2.Typer()) {
              return this.e1.Typer();
            }
            else {throw new Error("invalid type");}
          case Ebinop:
            switch(this.op){
              case Bobj:
              if (this.e1.Typer() == this.e2.Typer()) {
                return this.e1.Typer();
              }
              else {throw new Error("invalid type");}
              default:
              throw new Error("invalid type");
            }
          default:
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
}
/* Declarations */
class Declarations {}
  class Decl_variable extends Declarations {
    final LinkedList<String> v;
    final Type t;
    final LinkedList<String> l;
    Decl_variable(LinkedList<String> x) throws Exception {
      super();
      this.v = x;
      this.t = new Type("int");
      this.l = new LinkedList<String>();
      for (String s : x) {
        if (Typing.varType.containsKey(s)) {
          throw new Error("This variable name already exists");
        }
        Typing.varType.put(s,"int");
      }
    }
    Decl_variable(LinkedList<String> x, LinkedList<String> l) throws Exception {
      super();
      this.v = x;
      this.t = new Type("struct");
      this.l = l;
      for (String s : x) {
        if (Typing.varType.containsKey(s)) {
          throw new Error("This variable name already exists");
        }
        Typing.varType.put(s,x);
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
      HashSet<Decl_variable> unique = new HashSet<Decl_variable>(l);
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
      if (Typing.varType.containsKey(f)) {
        throw new Error("This name already exists.");
      }
      Typing.varType.put(f,t);
      LinkedList<String> typeArgs = new LinkedList<String>();
      for (Param p:this.l) {
        typeArgs.addLast(p.t.t);
      }
      Typing.funArgsType.put(f,typeArgs);
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
