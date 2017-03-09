package mini_c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Liveness {
    Map<Label, LiveInfo> info;

    Liveness(ERTLgraph g) {
        info = new HashMap<>();
        Map<Label, LiveInfo> lastInfo = new HashMap<>();
        int count = 0;
        do {
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
            count ++; //TODO: how many times? I don't get the Kildall algo
        } while( count < 33);
    }

    private void print(Set<Label> visited, Label l) {
        if (visited.contains(l)) return;
        visited.add(l);
        LiveInfo li = this.info.get(l);
        System.out.println("  " + String.format("%3s", l) + ": " + li);
        for (Label s: li.succ) print(visited, s);
    }

    void print(Label entry) { print(new HashSet<>(), entry); }

}

class LiveInfo {
    ERTL instr;
    Label[] succ;           // successeurs
    Set<Label> pred;        // prédécesseurs
    Set<Register> defs;     // définitions
    Set<Register> uses;     // utilisations
    Set<Register> ins;      // variables vivantes en entrée
    Set<Register> outs;     // variables vivantes en sortie

    public String toString() {
        return " def = " + this.defs + " use = " + this.uses + " in = " + ins + " out = " + outs;
    }
}