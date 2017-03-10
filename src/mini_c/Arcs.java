package mini_c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Arcs {
    Set<Register> prefs = new HashSet<>();
    Set<Register> intfs = new HashSet<>();
}

class Interference {
    Map<Register, Arcs> graph;

    Interference(Liveness lg) {
        graph = new HashMap<>();
        for ( Map.Entry<Label, LiveInfo> l : lg.info.entrySet()) {  // For every label in ERTL
            LiveInfo li = l.getValue();
            if ( (li.instr instanceof ERmbinop) && ( ((ERmbinop)li.instr).m == Mbinop.Mmov)) { // Mmov v w
                Register w = ((ERmbinop)li.instr).r1;
                Register v = ((ERmbinop)li.instr).r2;
                Arcs arcsv = new Arcs();
                Arcs arcsw = new Arcs();
                if (graph.get(v) != null) { arcsv = graph.get(v); }
                if (graph.get(w) != null) { arcsw = graph.get(w); }
                arcsw.prefs.add(v);
                arcsv.prefs.add(w);
                graph.put(v, arcsv);
                graph.put(w, arcsw);
            }
        }
        for ( Map.Entry<Label, LiveInfo> l : lg.info.entrySet()) {  // For every label in ERTL
            LiveInfo li = l.getValue();
            if ( (li.instr instanceof ERmbinop) && ( ((ERmbinop)li.instr).m == Mbinop.Mmov)) { // Mmov v w
                Register w = ((ERmbinop)li.instr).r1;
                Register v = ((ERmbinop)li.instr).r2;
                Arcs arcsv = graph.get(v);
                for ( Register r : li.outs) {
                    if ( (r != w) && (r != v) ) {   // for all the other registers in out
                        arcsv.intfs.add(r);         // Add as interference
                        graph.get(r).intfs.add(v);  // For both
                    }
                }
            }
            else {
                Register v = li.instr.getR();
                if (v != null) {
                    Arcs arcsv = graph.get(v);
                    if (arcsv == null) {        // Make sure I have an arcs (Mov $45 #r1 will make no entry for #r1)
                        arcsv = new Arcs();     // TODO: in the future I think this can be removed. If the entry is not created then we can delete the operation
                        graph.put(v, arcsv);
                    }
                    Register w = li.instr.getConflict();    // Does the ERTL has a conflict of registers?
                    if ( w != null) {
                        arcsv.intfs.add(w);         // Add as interference
                        graph.get(w).intfs.add(v);  // For both
                    }
                    for ( Register r : li.outs) {       // for all the other registers in out
                        if ( (r != v) ) {               // that is not v obviously
                            arcsv.intfs.add(r);         // Add as interference
                            graph.get(r).intfs.add(v);  // For both
                        }
                    }
                }
            }
        }
    }

    void print() {
        System.out.println("interference:");
        for (Register r: graph.keySet()) {
            Arcs a = graph.get(r);
            System.out.println("  " + r + " pref=" + a.prefs + " intf=" + a.intfs);
        }
    }
}
