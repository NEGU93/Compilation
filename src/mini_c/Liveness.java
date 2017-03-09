package mini_c;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Liveness {

    Map<Label, LiveInfo> info;

    Liveness(ERTLgraph g) {
        for ( Map.Entry<Label, ERTL> ertlg : g.graph.entrySet() ) { // For every label
            LiveInfo liveInfo = new LiveInfo();
            liveInfo.defs = ertlg.getValue().def();
            liveInfo.uses = ertlg.getValue().use();
            liveInfo.succ = ertlg.getValue().succ();
            liveInfo.instr = ertlg.getValue();
        }
    }

    private void print(Set<Label> visited, Label l) {
        if (visited.contains(l)) return;
        visited.add(l);
        LiveInfo li = this.info.get(l);
        System.out.println("  " + String.format("%3s", l) + ": " + li.toString());
        for (Label s: li.succ) print(visited, s);
    }

    void print(Label entry) { print(new HashSet<Label>(), entry); }

}

class LiveInfo {
    ERTL instr;
    Label[] succ;           // successeurs
    Set<Label> pred;        // prédécesseurs
    Set<Register> defs;     // définitions
    Set<Register> uses;     // utilisations
    Set<Register> ins;      // variables vivantes en entrée
    Set<Register> outs;     // variables vivantes en sortie

    /*String toString() {
        return " d = " + this.defs + " i = " + this.uses;
    }*/
}