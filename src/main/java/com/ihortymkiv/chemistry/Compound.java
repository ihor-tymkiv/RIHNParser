package com.ihortymkiv.chemistry;

import java.util.*;

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
}
