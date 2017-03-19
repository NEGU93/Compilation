package mini_c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

class Liveness {
	Map<Label, LiveInfo> info;

	// Liveness(ERTLgraph g) {
	// info = new HashMap<>();
	// //Map<Label, LiveInfo> lastInfo;
	// // boolean finish = false;
	// boolean finish;
	// int count = 0;
	// do {
	// finish = true;
	// //lastInfo = new;
	// // this is not yet the Kildall algorithm but the less effective
	// one. I don't use pred. (calcul de point fixe dans pdf)
	// for (Map.Entry<Label, ERTL> ertlg : g.graph.entrySet()) { // For every
	// label
	// LiveInfo liveInfo = new LiveInfo();
	//
	// liveInfo.defs = ertlg.getValue().def();
	// liveInfo.uses = ertlg.getValue().use();
	// liveInfo.succ = ertlg.getValue().succ();
	// liveInfo.instr = ertlg.getValue();
	//
	// liveInfo.outs = new HashSet<>();
	// liveInfo.ins = new HashSet<>();
	//
	// for (Label label : liveInfo.succ) {
	// if (info.get(label) != null) {
	// liveInfo.outs.addAll(info.get(label).ins);
	// }
	// }
	// liveInfo.ins = liveInfo.uses;
	// liveInfo.ins.addAll(liveInfo.outs);
	// liveInfo.ins.removeAll(liveInfo.defs);
	// if (!liveInfo.equals(this.info.get(ertlg.getKey()))) {
	// finish = false;
	// }
	// this.info.put(ertlg.getKey(), liveInfo);
	// }
	// /*I still don't have how to compare to know when I reach the
	// stationary state so I hardcode the 30 (I guess in 30 iterations it will
	// be done)
	// I implemented the function equals that uses the funciton equals of
	// Register and Label but still it doesn't work.
	// the problem is that (I think) when I copy lastInfo is a reference copy
	// and then a change in info chances lastInfo as well but I'm not sure
	// */
	// count++;
	// // if (count > 30) { finish = true; }
	// /*boolean equal = true;
	// for ( Map.Entry<Label, LiveInfo> liveInfo : this.info.entrySet() ) {
	// if (! liveInfo.getValue().equals( lastInfo.get( liveInfo.getKey() )) ) {
	// equal = false;
	// }
	// }
	// System.out.println("Stationary state: " + equal);*/
	//
	// } while(!finish);
	// //System.out.println("finished in "+count+" steps");
	//
	// }
	Liveness(ERTLgraph g) { // hoping that this is Kildall
		info = new HashMap<>();
		HashMap<Label, Boolean> labelsInStack = new HashMap<Label, Boolean>();
		Stack<Label> stack = new Stack<Label>();
		Label truc = new Label();

		for (Label l : g.graph.keySet()) {
			LiveInfo liveInfo = new LiveInfo();
			stack.push(l);
			labelsInStack.put(l, true);

			ERTL ertl = g.graph.get(l);
			liveInfo.defs = ertl.def();
			liveInfo.uses = ertl.use();
			liveInfo.succ = ertl.succ();
			liveInfo.instr = ertl;

			liveInfo.outs = new HashSet<>();
			liveInfo.ins = new HashSet<>();

			this.info.put(l, liveInfo);

		}

		boolean finish;
		while (!stack.empty()) {
			// finish = true;

			Label current = stack.pop();
			labelsInStack.put(current, false);

			// System.out.println("here " + stack.size());
			LiveInfo liveInfo = this.info.get(current);
			//ERTL ertl = liveInfo.instr;

			// liveInfo.defs = ertl.def();
			// liveInfo.uses = ertl.use();
			// liveInfo.succ = ertl.succ();
			// liveInfo.instr = ertl;
			int old_in = liveInfo.ins.size();

			//liveInfo.outs = new HashSet<>();
			//liveInfo.ins = new HashSet<>();

			// updating the out
			for (Label label : liveInfo.succ) {
					liveInfo.outs.addAll(this.info.get(label).ins);
			}
			// updating the in
//TODO this code should work and do the good algorithm but raises NPE
			liveInfo.ins.clear();
			liveInfo.ins.addAll(liveInfo.uses);
			for (Register r : liveInfo.outs) {
				if (!liveInfo.defs.contains(r)) {
					liveInfo.ins.add(r);
				}
			}
			
			//this code compiles but is wrong since putting liveInfo.ins=liveInfo.uses; make the pointers identical and modifies uses too
			//liveInfo.ins=liveInfo.uses;
			//liveInfo.ins.addAll(liveInfo.outs);
			//liveInfo.ins.removeAll(liveInfo.defs);
		
			// on teste si old_in et in sont égaux
			
			finish = old_in < liveInfo.ins.size();
			// System.out.println("here finish is "+finish);
			/*
			 * if (old_in != null && old_in.size() == liveInfo.ins.size()) { for
			 * (Register r : old_in) { if (!liveInfo.ins.contains(r)) { finish =
			 * false; break; } } }
			 */

			// updating the info
			// this.info.put(current, liveInfo);

			// for each pred we add it to the stack
			if (finish) {
				for (Label l1 : g.graph.keySet()) {
					boolean isSux = false;
					Label[] sux = info.get(l1).succ;
					for (int j = 0; j < sux.length; j++) {
						if (sux[j].name.equals(current.name)) {
							isSux = true;
						}
					}
					if (isSux && !labelsInStack.get(l1)) {
						stack.push(l1);
						labelsInStack.put(l1, true);
					}
				}
			}

		}

	}

	private void print(Set<Label> visited, Label l) {
		if (visited.contains(l))
			return;
		visited.add(l);
		LiveInfo li = this.info.get(l);
		System.out.println("  " + String.format("%3s", l) + ": " + li);
		for (Label s : li.succ)
			print(visited, s);
	}

	void print(Label entry) {
		print(new HashSet<>(), entry);
	}

	@Override
	public boolean equals(Object o) {
		Liveness that = (Liveness) o;
		for (Map.Entry<Label, LiveInfo> liveInfo : this.info.entrySet()) {
			if (!liveInfo.getValue().equals(that.info.get(liveInfo.getKey()))) {
				return false;
			}
		}
		return true;
	}
}

class LiveInfo {
	ERTL instr;
	Label[] succ; // successeurs
	Set<Label> pred; // prédécesseurs
	Set<Register> defs; // définitions
	Set<Register> uses; // utilisations
	Set<Register> ins; // variables vivantes en entrée
	Set<Register> outs; // variables vivantes en sortie

	@Override
	public String toString() {
		return " def = " + this.defs + " use = " + this.uses + " in = " + ins + " out = " + outs;
	}

	@Override
	public boolean equals(Object o) {
		LiveInfo that = (LiveInfo) o;
		if (that == null) {
			return false;
		}
		// TODO: I yet not check instr or pred
		if (!this.defs.equals(that.defs)) {
			return false;
		}
		if (!this.uses.equals(that.uses)) {
			return false;
		}
		if (!this.ins.equals(that.ins)) {
			return false;
		}
		if (!this.outs.equals(that.outs)) {
			return false;
		}
		return true;
	}
}
