package mini_c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Liveness {
    Map<Label, LiveInfo> info;

    Liveness(ERTLgraph g) {
        info = new HashMap<>();
        //Map<Label, LiveInfo> lastInfo;
        boolean finish = false;
        int count = 0;
        do {
            //lastInfo = info;
            // TODO: this is not yet the K... algorithm but the less effective one. I don't use pred
            for (Map.Entry<Label, ERTL> ertlg : g.graph.entrySet()) { // For every label
                LiveInfo liveInfo = new LiveInfo();

                liveInfo.defs = ertlg.getValue().def();
                liveInfo.uses = ertlg.getValue().use();
                liveInfo.succ = ertlg.getValue().succ();
                liveInfo.instr = ertlg.getValue();

                liveInfo.outs = new HashSet<>();
                liveInfo.ins = new HashSet<>();

                for (Label label : liveInfo.succ) {
                    if (info.get(label) != null) {
                        liveInfo.outs.addAll(info.get(label).ins);
                    }
                }
                liveInfo.ins = liveInfo.uses;
                liveInfo.ins.addAll(liveInfo.outs);
                liveInfo.ins.removeAll(liveInfo.defs);

                this.info.put(ertlg.getKey(), liveInfo);
            }
            //TODO: I still don't have how to compare to know when I reach the stationary state so I hardcode the 30
            count++;
            if (count > 30) { finish = true; }

            /*boolean equal = true;
            for ( Map.Entry<Label, LiveInfo> liveInfo : this.info.entrySet() ) {
                if (! liveInfo.getValue().equals( lastInfo.get( liveInfo.getKey() )) ) {
                    equal = false;
                }
            }
            System.out.println("Stationary state: " + equal);*/
        } while(!finish);
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
        //TODO: I yet not check instr or pred
        if (! this.defs.equals(that.defs)) { return false; }
        if (! this.uses.equals(that.uses)) { return false; }
        if (! this.ins.equals(that.ins))   { return false; }
        if (! this.outs.equals(that.outs)) { return false; }
        return true;
    }
}