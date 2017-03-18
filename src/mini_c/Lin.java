package mini_c;

import java.util.HashSet;
import java.util.Map;

/*
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
    /** Instructions */
    @Override public void visit(Laccess_global o) {
        asm.movq(o.s, o.r.toString());
        lin(o.l);
    }
    @Override public void visit(Lassign_global o) {
        asm.movq(o.r.toString(), o.s);
        lin(o.l);
    }
    @Override public void visit(Lload o) {
        asm.movq(o.i + "(" + o.r1 + ") ", o.r2.toString());
        lin(o.l);
    }
    @Override public void visit(Lstore o) {
        asm.movq(o.r1.toString(), o.i + "(" + o.r2 + ") ");
        lin(o.l);
    }
    @Override public void visit(Lmubranch o) {
        if (!visited.contains(o.l2) || visited.contains(o.l1)) {  // If l2 is not yet produced or if both are
            if (o.m instanceof Mjz) {
                asm.jz(o.l1.toString());
            } else if (o.m instanceof Mjnz) {
                asm.jnz(o.l1.toString());
            } else if (o.m instanceof Mjlei) {
                asm.jle(o.l1.toString());
            } else if (o.m instanceof Mjgi) { // the if is unnecessary but I find it more clear
                asm.jg(o.l1.toString());
            }
            if (!visited.contains(o.l2)) {  // case l2 is not visited yet
                asm.needLabel(o.l1); // TODO: not sayed by the lecture but it makes no sense not to add it.
                lin(o.l2);
                lin(o.l1);
            }
            else {  // case l1 & l2 were visited
                asm.jmp(o.l2.toString());
            }
        }
        else if (!visited.contains(o.l1) ) {
            if (o.m instanceof Mjz) {
                asm.jnz(o.l2.toString());
            } else if (o.m instanceof Mjnz) {
                asm.jz(o.l2.toString());
            } else if (o.m instanceof Mjlei) {
                asm.jg(o.l2.toString());
            } else if (o.m instanceof Mjgi) { // the if is unnecessary but I find it more clear
                asm.jle(o.l2.toString());
            }
            lin(o.l1);
            lin(o.l2);
        }
    }
    @Override public void visit(Lmbbranch o) {
        Label L2 = o.l1;
        Label L3 = o.l2;
        if (!visited.contains(L3) || visited.contains(L2) ) {
            asm.cmpq(o.r1.toString(), o.r2.toString());
            switch (o.m) {
                case Mjl:
                    asm.jl(L2.toString());
                    break;
                case Mjle:
                    asm.jle(L2.toString());
                    break;
                case Mjeqeq:
                    asm.je(L2.toString());
                    break;
                case Mjneq:
                    asm.jne(L2.toString());
                    break;
                case Mjg:
                    asm.jg(L2.toString());
                    break;
                case Mjge:
                    asm.jge(L2.toString());
            }
            if (!visited.contains(L3)) {
                asm.needLabel(o.l1); // TODO: not sayed by the lecture but it makes no sense not to add it.
                lin(L3);
                lin(L2);
            }
            else {
                asm.jmp(L3.toString());
            }
        }
        else {
            switch (o.m) {
                case Mjl:
                    asm.jge(L3.toString());
                    break;
                case Mjle:
                    asm.jg(L3.toString());
                    break;
                case Mjeqeq:
                    asm.jne(L3.toString());
                    break;
                case Mjneq:
                    asm.je(L3.toString());
                    break;
                case Mjg:
                    asm.jle(L3.toString());
                    break;
                case Mjge:
                    asm.jl(L3.toString());
            }
            asm.needLabel(L3);
            lin(L2);
            lin(L3);
        }
    }
    @Override public void visit(Lgoto o) {
        /* L1 : goto -> L2 */
        if(visited.contains(o.l)) {
            asm.jmp(o.l.toString());
        }
        // TODO: it says "produire l'etiquette L1" but I don't know what it means. Possible bug then here
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
        if (o.m instanceof Maddi) {
            asm.addq( "$" + Integer.toString(((Maddi) o.m).n), o.o.toString());
        }
        else if (o.m instanceof Msetei) {
            asm.sete(o.m.toString() + " " + o.o.toString());
        }
        else if (o.m instanceof Msetnei) {
            asm.setne(o.m.toString() + " " + o.o.toString());
        }
        lin(o.l);
    }
    @Override public void visit(Lmbinop o) {
        switch (o.m) {
            case Mmov:
                asm.movq(o.o1.toString(), o.o2.toString());
                break;
            case Madd:
                System.out.println("Here I am with: " + o.o1.toString() + o.o2.toString());
                asm.addq(o.o1.toString(), o.o2.toString());
                break;
            case Msub:
                asm.subq(o.o1.toString(), o.o2.toString());
                break;
            case Mmul:
                asm.imulq(o.o1.toString(), o.o2.toString());
                break;
            case Mdiv:
                // I don't know what to do here. div has only one register input. How does it work? I changed to 2 reg. Hope is ok
                asm.idivq(o.o1.toString(), o.o2.toString());
                break;
            case Mand:
                asm.andq(o.o1.toString(), o.o2.toString());
                break;
            case Mor:
                asm.orq(o.o1.toString(), o.o2.toString());
                break;
            default:
                throw new Error("Still todo because I don't know what to do with the rest. operation " + o.m.toString());
        }
        lin(o.l);
    }
    @Override public void visit(Lpush_param o) {
        asm.pushq(o.o.toString());
    }
    @Override public void visit(Lcall o) {
        asm.call(o.s);
        lin(o.l);
    }
    @Override public void visit(LTLfun o) {
        // TODO: mmm here? shall I do something? the code is working so I guess not
    }
    @Override public void visit(LTLfile o) {
        // TODO: mmm here? shall I do something? the code is working so I guess not
    }

    X86_64 getAsm(){ return asm; }
    void addGlobal(String x) {
        asm.dlabel(x);
    }
}
