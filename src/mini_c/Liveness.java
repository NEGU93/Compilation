package mini_c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

class Liveness {
	Map<Label, LiveInfo> info;

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
			Label current = stack.pop();
			labelsInStack.put(current, false);

			LiveInfo liveInfo = this.info.get(current);

			int old_in = liveInfo.ins.size();

			// updating the out
			for (Label label : liveInfo.succ) {
					liveInfo.outs.addAll(this.info.get(label).ins);
			}

			// updating the in
			liveInfo.ins.clear();
			liveInfo.ins.addAll(liveInfo.uses);
			for (Register r : liveInfo.outs) {
				if (!liveInfo.defs.contains(r)) {
					liveInfo.ins.add(r);
				}
			}
			// on teste si old_in et in sont égaux
			
			finish = old_in < liveInfo.ins.size();

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
