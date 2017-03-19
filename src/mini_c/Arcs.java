package mini_c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class Arcs {
    Set<Register> prefs = new HashSet<>();
    Set<Register> intfs = new HashSet<>();
}

class Interference {
    Map<Register, Arcs> graph;

    Interference(Liveness lg) {
        graph = new HashMap<>();
        // Add preference
        for ( Map.Entry<Label, LiveInfo> l : lg.info.entrySet()) {  // For every label in ERTL
            LiveInfo li = l.getValue();
            if ( (li.instr instanceof ERmbinop) && ( ((ERmbinop)li.instr).m == Mbinop.Mmov)) { // Mmov v w
                Register w = ((ERmbinop)li.instr).r1;   //get both registers involved
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
        //Add interference
        // (I don't remember why I did this separatelly but there must be a reason... And it there is none then I'm too afraid to change it now)
        for ( Map.Entry<Label, LiveInfo> l : lg.info.entrySet()) {  // For every label in ERTL
            LiveInfo li = l.getValue();
            if ( (li.instr instanceof ERmbinop) && ( ((ERmbinop)li.instr).m == Mbinop.Mmov)) { // Mmov v w
                Register w = ((ERmbinop)li.instr).r1;
                Register v = ((ERmbinop)li.instr).r2;
                Arcs arcsv = graph.get(v);
                for ( Register r : li.outs) {       // for all the other registers in out
                    if ( (r != w) && (r != v) ) {   // that are not v or w obviously
                        arcsv.intfs.add(r);         // Add as interference
                        graph.putIfAbsent(r, new Arcs());
                        graph.get(r).intfs.add(v);  // For both
                    }
                }
            }
            else {
                Register v = li.instr.getR();
                if (v != null) {
                    graph.putIfAbsent(v, new Arcs());       // Make sure I have it
                    Arcs arcsv = graph.get(v);
                    Register w = li.instr.getConflict();    // Does the ERTL has a conflict of registers?
                    if ( w != null) {   //
                        /* Maybe this is not necessary and I'm adding conflicts that should not be there
                        * This is not explained in the polycopie or the lectures but thinking about it I think this should be here
                        * If not I'm just adding interference which will make the final code less efficient in the worst case scenario
                        * But it will work anyway. */
                        arcsv.intfs.add(w);         // Add as interference
                        graph.putIfAbsent(w, new Arcs());   // Make sure w is in the graph
                        graph.get(w).intfs.add(v);  // For both
                    }
                    for ( Register r : li.outs) {       // for all the other registers in out
                        if ( (r != v) ) {               // that is not v obviously
                            graph.putIfAbsent(r, new Arcs());
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
