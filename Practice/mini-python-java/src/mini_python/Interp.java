package mini_python;

import java.util.HashMap;
import java.util.Iterator;

/* Les valeurs de mini-Python

   - une différence notable avec Python : on
     utilise ici le type int alors que les entiers de Python sont de
     précision arbitraire ; on pourrait utiliser les grands entiers de Java
     mais on choisit la facilité
   - ce que Python appelle une liste est en réalité un tableau
     redimensionnable ; dans le fragment considéré ici, il n'y a pas
     de possibilité d'en modifier la longueur, donc un simple tableau
     convient */

abstract class Value implements Comparable<Value> {
	abstract boolean isFalse();

	boolean isTrue() {
		return !this.isFalse();
	}

	int asInt() {
		if (!(this instanceof Vint))
			throw new Error("integer expected");
		return ((Vint) this).i;
	}

	Vlist asList() {
		if (!(this instanceof Vlist))
			throw new Error("list expected");
		return (Vlist) this;
	}
}

class Vnone extends Value {
	@Override
	boolean isFalse() {
		return true; 
	}

	@Override
	public String toString() {
		return "None";
	}

	@Override
	public int compareTo(Value o) {
		return o instanceof Vnone ? 0 : -1;
	}
}

class Vbool extends Value {
	final boolean b;

	Vbool(boolean b) {
		this.b = b;
	}

	@Override
	boolean isFalse() {
		return !this.b;
	}

	@Override
	public String toString() {
		return this.b ? "True" : "False";
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof Vnone)
			return 1;
		if (o instanceof Vbool) {
			boolean ob = ((Vbool) o).b;
			return b ? (ob ? 0 : 1) : (ob ? -1 : 0);
		}
		return -1;
	}
}

class Vint extends Value {
	final int i;

	Vint(int i) {
		this.i = i;
	}

	@Override
	boolean isFalse() {
		return this.i == 0 ? true : false;
	}

	@Override
	public String toString() {
		return "" + this.i;
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof Vnone || o instanceof Vbool)
			return 1;
		if (o instanceof Vint)
			return this.i - o.asInt();
		return -1;
	}
}

class Vstring extends Value {
	final String s;

	Vstring(String s) {
		this.s = s;
	}

	@Override
	boolean isFalse() {
		return this.s == "" ? true : false;
	}

	@Override
	public String toString() {
		return this.s;
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof Vnone || o instanceof Vbool || o instanceof Vint)
			return 1;
		if (o instanceof Vstring)
			return this.s.compareTo(((Vstring) o).s);
		return -1;
	}
}

class Vlist extends Value {
	final Value[] l;

	Vlist(int n) {
		this.l = new Value[n];
	}

	Vlist(Value[] l1, Value[] l2) {
		this.l = new Value[l1.length + l2.length];
		System.arraycopy(l1, 0, l, 0, l1.length);
		System.arraycopy(l2, 0, l, l1.length, l2.length);
	}

	@Override
	boolean isFalse() {
		return l.length == 0 ? true : false;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[");
		for (int i = 0; i < this.l.length; i++) {
			if (i != 0)
				b.append(", ");
			b.append(this.l[i]);
		}
		b.append("]");
		return b.toString();
	}

	@Override
	public int compareTo(Value o) {
		if (!(o instanceof Vlist))
			return -1;
		Value[] ol = ((Vlist) o).l;
		int n1 = this.l.length, n2 = ol.length;
		int i1 = 0, i2 = 0;
		for (; i1 < n1 && i2 < n2; i1++, i2++) {
			Value v1 = this.l[i1];
			Value v2 = ol[i2];
			int c = v1.compareTo(v2);
			if (c != 0)
				return c;
		}
		if (i1 < n1)
			return 1;
		if (i2 < n2)
			return -1;
		return 0;
	}
}

/* on utilise l'exception ci-dessous pour interpréter la construction return
 * de mini-Python */

class Return extends Exception {
	private static final long serialVersionUID = 1L;
	
	final Value v;

	Return(Value v) { this.v = v; }
}

class Todo extends Error {
	private static final long serialVersionUID = 1L;
}

/* l'interprète */

class Interp implements Interpreter {

	// variables de mini-Python (globales, locales, arguments)
	HashMap<String, Value> vars;

	Interp() {
		this.vars = new HashMap<String, Value>();
	}

	// les définitions de fonctions
	static HashMap<String, Def> functions = new HashMap<String, Def>();

	// interprétation d'une opération binaire sur deux valeurs
	static Value binop(Binop op, Value v1, Value v2) {
		switch (op) {
		case Bsub: // Do operations. For the first ones, only the int is considered.
			if (v1 instanceof Vint && v2 instanceof Vint) {
				return new Vint(v1.asInt() - v2.asInt());
			}
			break;
		case Bmul:
			if (v1 instanceof Vint && v2 instanceof Vint) {
				return new Vint(v1.asInt() * v2.asInt());
			}
			break;
		case Bdiv:
			if (v1 instanceof Vint && v2 instanceof Vint) {
				if ( v2.asInt() != 0) { return new Vint(v1.asInt() / v2.asInt()); } // Here I call 2 times asInt. Perhaps is no efficient
				else { return new Vnone(); } //Don't really sure what I have to do in a divide by zero case
			}
			break;
		case Bmod:
			if (v1 instanceof Vint && v2 instanceof Vint) {
				if ( v2.asInt() != 0) { return new Vint(v1.asInt() % v2.asInt()); } // Here I call 2 times asInt. Perhaps is no efficient
				else { return new Vnone(); } //Don't really sure what I have to do in a divide by zero case
			}
			break;
		case Badd:
			if (v1 instanceof Vint && v2 instanceof Vint) {
				return new Vint(v1.asInt() + v2.asInt());
			}
			if (v1 instanceof Vstring && v2 instanceof Vstring) {
				return new Vstring(v1.toString() + v2.toString());
			}
			if (v1 instanceof Vlist && v2 instanceof Vlist) {
				return new Vlist( ((Vlist)v1).l , ((Vlist)v2).l );
			}
			break;
		case Beq:
			return new Vbool(v1.compareTo(v2) == 0);
		case Bneq:
			return new Vbool(v1.compareTo(v2) != 0);
		case Blt:
			return new Vbool(v1.compareTo(v2) < 0);
		case Ble:
			return new Vbool(v1.compareTo(v2) <= 0);
		case Bgt:
			return new Vbool(v1.compareTo(v2) > 0);
		case Bge:
			return new Vbool(v1.compareTo(v2) >= 0);
		default:
		}
		throw new Error("unsupported operand types");
	}

	// interprétation des différentes constructions de mini-Python

	@Override
	public Value interp(Ecst e) {
		return e.c.interp();
	}

	@Override
	public Value interp(Ebinop e) {
		Value v1 = e.e1.accept(this);
		Value v2 = e.e2.accept(this);
		switch (e.op) {
		case Band:
			return new Vbool(v1.isTrue() && v2.isTrue()); //Other way: Vbool(v1.compareTo(v2) == 0);
		case Bor:
			return new Vbool(v1.isTrue() || v2.isTrue());
		default:
			return binop(e.op, v1, e.e2.accept(this));
		}
	}

	@Override
	public Value interp(Eunop e) {
		switch (e.op) {
		case Unot: // ~b
			return new Vbool(e.e.accept(this).isFalse());
		case Uneg: // if 4 -> -4
			return new Vint(-e.e.accept(this).asInt());
		}
		throw new Error("unreachable");
	}

	@Override
	public Value interp(Ecall e) {
		switch (e.f) {
		case "len":
			if (e.l.size() != 1) { throw new Error("more than 1 list not supported");	}
			Value v = e.l.get(0).accept(this);
			if (v instanceof Vlist) { return new Vint( ((Vlist)v).l.length ); }
			else if (v instanceof Vstring) { return new Vint( ((Vstring) v).s.length() ); }
			else { throw new Error("This value has no length"); }
		case "range":
			if (e.l.size() != 1) { throw new Error("more than 1 element not supported"); }
			int n = e.l.get(0).accept(this).asInt();
			if (n <= 0) { throw new Error("Negative or Null size for range"); }
			Vlist vlist = new Vlist(n);
			for (int i = 0; i < n; i++) {
				vlist.l[i] = new Vint(i); // Create array [0, 1, 2, ..., n];
			}
		default:
			Def def = functions.get(e.f);
			if (def == null) { throw new Error("unbound function " + e.f); }
			if (e.l.size() != def.l.size()) { throw new Error("bad arity"); } // ASK: isn't this always true?
			Interp variables = new Interp();
			Iterator<String> it = def.l.iterator();
			for (Expr e1 : e.l)
				variables.vars.put(it.next(), e1.accept(this));
			try {
				def.s.accept(variables);
				return new Vnone();
			} catch (Return r) {
				return r.v;
			}
		}
	}

	@Override
	public Value interp(Elist e) {
		Vlist v = new Vlist(e.l.size());
		for (int i = 0; i < e.l.size(); i++) {
			v.l[i] = e.l.get(i).accept(this);
		}
		return v;
	}

	@Override
	public Value interp(Eleft e) {
		return e.lv.accept(this);
	}

	@Override
	public Value interp(Lident lv) {
		Value v = vars.get(lv.s);
		if (v == null) { throw new Error("unbound variable" + lv.s); }
		return v;
	}

	@Override
	public Value interp(Lnth lv) { // returns the value of the list e1 at the index e2
		Vlist v = lv.e1.accept(this).asList();
		int i = lv.e2.accept(this).asInt();
		if (i < 0 || i >= v.l.length) {	throw new Error("index out of bounds"); }
		return v.l[i];
	}

	@Override
	public void assign(Lident lv, Expr e) {
		vars.put(lv.s, e.accept(this));
	}

	@Override
	public void assign(Lnth lv, Expr e) {
		Vlist v = lv.e1.accept(this).asList();
		int i = lv.e2.accept(this).asInt();
		if (i < 0 || i >= v.l.length)
			throw new Error("index out of bounds");
		v.l[i] = e.accept(this);
	}

	/* instructions */
	
	@Override
	public void interp(Seval s) throws Return {
		s.e.accept(this);
	}

	@Override
	public void interp(Sprint s) throws Return {
		System.out.println(s.e.accept(this).toString());
	}

	@Override
	public void interp(Sblock s) throws Return {
		for (Stmt st: s.l)
			st.accept(this);
	}

	@Override
	public void interp(Sif s) throws Return {
		if ( s.e.accept(this).isTrue() ) { s.s1.accept(this); }
		else { s.s2.accept(this); }
	}

	@Override
	public void interp(Sassign s) throws Return {
		s.lv.assign(this, s.e);
	}

	@Override
	public void interp(Sreturn s) throws Return {
		throw new Return(s.e.accept(this)); // ASK: why not something like function.put(...) and... where do I do function.put????
	}

	@Override
	public void interp(Sfor s) throws Return {
		Vlist l = s.e.accept(this).asList();
		for (Value v: l.l) {
			vars.put(s.x, v);
			s.s.accept(this);
		}
	}

}
