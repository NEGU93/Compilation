package mini_c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
class Liveness {
    Map<Label, LiveInfo> info;
   
//    Liveness(ERTLgraph g) {
//        info = new HashMap<>();
//        //Map<Label, LiveInfo> lastInfo;
//       // boolean finish = false;
//        boolean finish;
//        int count = 0;
//        do {
//        	finish = true;
//            //lastInfo = new;
//            // TODO: this is not yet the Kildall algorithm but the less effective one. I don't use pred. (calcul de point fixe dans pdf)
//            for (Map.Entry<Label, ERTL> ertlg : g.graph.entrySet()) { // For every label
//                LiveInfo liveInfo = new LiveInfo();
//
//                liveInfo.defs = ertlg.getValue().def();
//                liveInfo.uses = ertlg.getValue().use();
//                liveInfo.succ = ertlg.getValue().succ();
//                liveInfo.instr = ertlg.getValue();
//
//                liveInfo.outs = new HashSet<>();
//                liveInfo.ins = new HashSet<>();
//
//                for (Label label : liveInfo.succ) {
//                    if (info.get(label) != null) {
//                        liveInfo.outs.addAll(info.get(label).ins);
//                    }
//                }
//                liveInfo.ins = liveInfo.uses;
//                liveInfo.ins.addAll(liveInfo.outs);
//                liveInfo.ins.removeAll(liveInfo.defs);
//                if (!liveInfo.equals(this.info.get(ertlg.getKey()))) {
//                	finish = false;
//                }
//                this.info.put(ertlg.getKey(), liveInfo);
//            }
//            /*TODO: I still don't have how to compare to know when I reach the stationary state so I hardcode the 30 (I guess in 30 iterations it will be done)
//                I implemented the function equals that uses the funciton equals of Register and Label but still it doesn't work.
//                the problem is that (I think) when I copy lastInfo is a reference copy and then a change in info chances lastInfo as well but I'm not sure
//            */
//            count++;
//           // if (count > 30) { finish = true; }
//            /*boolean equal = true;
//            for ( Map.Entry<Label, LiveInfo> liveInfo : this.info.entrySet() ) {
//                if (! liveInfo.getValue().equals( lastInfo.get( liveInfo.getKey() )) ) {
//                    equal = false;
//                }
//            }
//            System.out.println("Stationary state: " + equal);*/
//            
//        } while(!finish);
//        //System.out.println("finished in "+count+" steps");
//
//    }
	Liveness(ERTLgraph g) { //hoping that this is Kildall
		info = new HashMap<>();
		HashMap<Label,Boolean> labelsInStack = new HashMap<Label,Boolean>();
		Stack<Label> stack = new Stack<Label>();
		Label truc=new Label();
		LiveInfo liveInfo = new LiveInfo();
		for (Label l : g.graph.keySet()) {
			stack.push(l);
			labelsInStack.put(l, true);
			
			ERTL ertl = g.graph.get(l);
			liveInfo.defs = ertl.def();
			liveInfo.uses = ertl.use();
			liveInfo.succ = ertl.succ();
			liveInfo.instr = ertl;
			this.info.put(l, liveInfo);
			
		}
		
		//ERTL ertl2 = g.graph.get(stack.peek());
		//System.out.println("here "+ertl2);
		boolean finish;
		while (!stack.empty()) {
			finish = true;
			
			Label current = stack.pop();
			labelsInStack.put(current,false);
			ERTL ertl = g.graph.get(current);
			System.out.println("here "+stack.size());
			
			liveInfo.defs = ertl.def();
			liveInfo.uses = ertl.use();
			liveInfo.succ = ertl.succ();
			liveInfo.instr = ertl;
			Set<Register> old_in =null;
			
			if (this.info.get(current)!=null) {
			old_in = this.info.get(current).ins;
			}
		

			liveInfo.outs = new HashSet<>();
			liveInfo.ins = new HashSet<>();

			// updating the out
			for (Label label : liveInfo.succ) {
				if (info.get(label) != null) {
					liveInfo.outs.addAll(info.get(label).ins);
				}
				// updating the in
				liveInfo.ins = liveInfo.uses;
				liveInfo.ins.addAll(liveInfo.outs);
				liveInfo.ins.removeAll(liveInfo.defs);
				// on teste si old_in et in sont égaux
				if (old_in != null && old_in.size() == liveInfo.ins.size()) {
					for (Register r : old_in) {
						if (!liveInfo.ins.contains(r)) {
							finish = false;
							break;
						}
					}
				} else {
					finish = false;
				}
				
				//updating the info
				this.info.put(current, liveInfo);
				
				//for each pred we add it to the stack
				if (!finish) {
					for (Label l : g.graph.keySet()) {
						boolean isSux = false;
						Label[] sux = info.get(l).succ;
						for (int j = 0; j < sux.length; j++) {
							if (sux[j].name.equals(current.name)) {
								isSux = true;
							}
						}
						if (isSux && !labelsInStack.get(l)) {
							stack.push(l);
							labelsInStack.put(l,true);
						}
					}
				}

			}
		}

	}
    
    private void print(Set<Label> visited, Label l) {
        if (visited.contains(l)) return;
        visited.add(l);
        LiveInfo li = this.info.get(l);
        System.out.println("  " + String.format("%3s", l) + ": " + li);
        for (Label s: li.succ) print(visited, s);
    }

    void print(Label entry) { print(new HashSet<>(), entry); }

    @Override
    public boolean equals(Object o) {
        Liveness that = (Liveness)o;
        for ( Map.Entry<Label, LiveInfo> liveInfo : this.info.entrySet() ) {
            if (! liveInfo.getValue().equals(that.info.get(liveInfo.getKey())) ) {
                return false;
            }
        }
        return true;
    }
}

class LiveInfo {
    ERTL instr;
    Label[] succ;           // successeurs
    Set<Label> pred;        // prédécesseurs
    Set<Register> defs;     // définitions
    Set<Register> uses;     // utilisations
    Set<Register> ins;      // variables vivantes en entrée
    Set<Register> outs;     // variables vivantes en sortie

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
        //TODO: I yet not check instr or pred
        if (! this.defs.equals(that.defs)) { return false; }
        if (! this.uses.equals(that.uses)) { return false; }
        if (! this.ins.equals(that.ins))   { return false; }
        if (! this.outs.equals(that.outs)) { return false; }
        return true;
    }
}