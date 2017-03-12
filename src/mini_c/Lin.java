package mini_c;

import java.util.HashSet;
import java.util.Map;

/**
 * Created by NEGU on 3/12/2017.
 */
class Lin implements LTLVisitor {
    private LTLgraph cfg;           // graphe en cours de traduction
    private X86_64 asm;             // code en cours de construction
    private HashSet<Label> visited; // instructions déjà traduites

    Lin() {
        asm = new X86_64();
        visited = new HashSet<>();
        asm.globl("main");
    }
    /* LTL to asm*/
    void translateFunction(LTLfun f) {
        cfg = f.body;
        asm.label(f.name);
        lin(f.entry);
    }

    private void lin(Label l) {
        if (visited.contains(l)) {
            asm.needLabel(l);
            asm.jmp(l.name);
        } else {
            visited.add(l);
            asm.label(l);
            cfg.graph.get(l).accept(this);
        }
    }

    @Override public void visit(Laccess_global o) {

    }
    @Override public void visit(Lassign_global o) {

    }
    @Override public void visit(Lload o) {

    }
    @Override public void visit(Lstore o) {

    }
    @Override public void visit(Lmubranch o) {

    }
    @Override public void visit(Lmbbranch o) {

    }
    @Override public void visit(Lgoto o) {
        if(visited.contains(o.l)) {
            asm.jmp(o.toString());
        }
        lin(o.l);
    }
    @Override public void visit(Lreturn o) {
        asm.ret();
    }
    @Override public void visit(Lconst o) {
        asm.movq(o.i, o.o.toString());
        lin(o.l);
    }
    @Override public void visit(Lmunop o) {

    }
    @Override public void visit(Lmbinop o) {

    }
    @Override public void visit(Lpush_param o) {

    }
    @Override public void visit(Lcall o) {

    }
    @Override public void visit(LTLfun o) {

    }
    @Override public void visit(LTLfile o) {

    }

    X86_64 getAsm(){ return asm; }
}
