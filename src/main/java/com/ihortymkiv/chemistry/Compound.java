package com.ihortymkiv.chemistry;

import java.util.*;
import java.util.function.Consumer;

/**
 * Graph representation of a chemical compound.
 */
public class Compound {
    private final List<Atom> atoms = new ArrayList<>();

    /**
     * Return a copy of the set containing atoms.
     * @return copy of the set of atoms
     */
    public List<Atom> getAtoms() {
        return new ArrayList<>(this.atoms);
    }

    public void addAtom(Atom atom) {
        Objects.requireNonNull(atom, "Atom cannot be null.");
        this.atoms.add(atom);
    }

    /**
     * Traverse the graph using BFS and execute atomConsumer with each node.
     * @param start start node
     * @param atomConsumer function that takes a node
     */
    public static void BFS(Atom start, Consumer<Atom> atomConsumer) {
        Queue<Atom> queue = new LinkedList<>();
        HashSet<Atom> seen = new HashSet<>();
        seen.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            Atom atom = queue.remove();
            atomConsumer.accept(atom);
            for (Atom neighbour : atom.getBondedAtoms()) {
                if (!seen.contains(neighbour)) {
                    seen.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }
    }
}
