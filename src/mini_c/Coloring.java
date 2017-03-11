package mini_c;

import java.util.*;

import static mini_c.Register.allocatable;

/**
 * Created by NEGU on 3/11/2017.
 */
class Coloring {
    Map<Register, Operand> colors = new HashMap<>();
    private Map<Register, Set<Register>> todo = new HashMap<>();
    int nlocals = 0;                                    // nombre d'emplacements sur la pile

    Coloring(Interference ig) {
        /* Make a list of all the registers to be assigned */
        for ( Map.Entry<Register, Arcs> i : ig.graph.entrySet() ) { // Get every register
            Register r = i.getKey();
            if (!allocatable.contains(r)) {                         // If it's a pseudo Register
                Set<Register> posibleColors = new HashSet<>();
                for ( Register allocatReg : allocatable ) {             // Add as possible colors every allocatable
                    if ( ! i.getValue().intfs.contains(allocatReg)) {   // that it's not in the interference
                        posibleColors.add(allocatReg);
                    }
                }
                todo.put(r, posibleColors);
            }
        }
        //printTodoList(todo);
        do {} while (onlyOnePossibility(ig)); // Do this until I have no only one posibility left
        do {} while (onlyOnePossibility2(ig));
        do {} while (preferredOne(ig));
        do {} while (assignTheRest(ig));
        spillRegisters();
        if (!todo.isEmpty()) { throw new Error("The todolist is still not empty"); }
    }

    /* This function checks from all the registers, if they have only one posible option and if that option is the prefered */
    private boolean onlyOnePossibility(Interference ig) {
        for (Map.Entry<Register, Set<Register>> tl : todo.entrySet()) { // go for every register in the todolist
            if (tl.getValue().size() == 1) {                    // If it has only one posible color
                Iterator<Register> iterator = tl.getValue().iterator();
                Register onlyPossible = iterator.next();         // The only posible color
                if ( ig.graph.get(tl.getKey()).prefs.contains(onlyPossible) ){ // Only one option and it's the prefered! good!
                    return addColour(tl.getKey(), onlyPossible, ig);
                }
             }
        }
        return false; // I have checked everyone and no one has only one possible that equals the prefered
    }
    /* This function checks from all the registers, if they have only one posible option */
    private boolean onlyOnePossibility2(Interference ig) {
        for (Map.Entry<Register, Set<Register>> tl : todo.entrySet()) { // go for every register in the todolist
            if (tl.getValue().size() == 1) {                    // If it has only one possible color
                Iterator<Register> iterator = tl.getValue().iterator();
                Register onlyPossible = iterator.next();         // The only possible color
                return addColour(tl.getKey(), onlyPossible, ig);                    // Let's try again to be sure there is not another one.
            }
        }
        return false; // I have checked everyone and no one has only one possible that equals the preferred
    }
    /* Check if the preferred is on the possibility list */
    private boolean preferredOne(Interference ig) {
        for (Map.Entry<Register, Set<Register>> tl : todo.entrySet()) { // go for every register in the todolist
            for ( Register preferredRegister : ig.graph.get(tl.getKey()).prefs ) {   // For every preferred register (Only one most of the time right?)
                if (tl.getValue().contains(preferredRegister)) { // If the preferred is possible
                    return addColour(tl.getKey(), preferredRegister, ig);
                }
            }
        }
        return false;
    }
    /* Ok, just do the rest of the list with whatever they can */
    private boolean assignTheRest(Interference ig) {
        for (Map.Entry<Register, Set<Register>> tl : todo.entrySet()) { // go for every register in the todolist
           if ( !tl.getValue().isEmpty() ) {    // if I have a possibility
               Iterator<Register> iterator = tl.getValue().iterator();
               Register possible = iterator.next();              // Get that possibility
               return addColour(tl.getKey(), possible, ig);
           }
        }
        return false;
    }
    /* Spill the others */
    private void spillRegisters() {
        // TODO: nlocals can actually be only used here right? instead of global? check is it's the case
        for (Map.Entry<Register, Set<Register>> tl : todo.entrySet()) { // For every register still in here
            if ( !tl.getValue().isEmpty() ) { throw new Error("Wanted to spill one that still has possibilities"); }
            Operand operand = new Spilled(nlocals);
            colors.put(tl.getKey(), operand);
            todo.remove(tl.getKey());
            nlocals++;
        }
    }

    /* Assigns a real Register to a pseudo register and delete it from the todolist*/
    private boolean addColour(Register pseudoRegister, Register realRegister, Interference interference) {
        Operand operand = new Reg(realRegister); // Create the register
        colors.put(pseudoRegister, operand);       // get it to the colored graph
        removeRegisterAsOption(realRegister, interference.graph.get(pseudoRegister).intfs); // Remove the new register from the interferences
        todo.remove(pseudoRegister);       // remove it from the todolist
        return true;                    // Let's try again to be sure there is not another one.
    }
    private void removeRegisterAsOption(Register toBeRemoved, Set<Register> fromHere) {
        if (fromHere.isEmpty()) { return; }
        for ( Register r : fromHere) { // for all the registers interferences
            if (todo.containsKey(r)) {
                todo.get(r).remove(toBeRemoved);
            }
        }
    }

    public Operand get(Register register) {
        if (allocatable.contains(register)) { return new Reg(register); }
        else { return colors.get(register); }
    }

    void print() {
        System.out.println("coloring output:");
        for (Register r: colors.keySet()) {
            Operand o = colors.get(r);
            System.out.println("  " + r + " --> " + o);
        }
    }
    void printTodoList() {
        for (Map.Entry<Register, Set<Register>> tl : todo.entrySet()) {
            String key = tl.getKey().toString();
            String value = " -> ";
            for (Register r : tl.getValue()) {
                value += ( " " + r.toString() + ";");
            }
            System.out.println(key + value);
        }
    }
}
