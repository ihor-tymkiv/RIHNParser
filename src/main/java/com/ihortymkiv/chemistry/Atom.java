package com.ihortymkiv.chemistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an atom.<p>
 *
 * Can be thought of as a vertex in our graph representation of a chemical compound.
 */
public class Atom {
    private static int counter = 0;

    private final int id;
    private final ChemicalElement chemicalElement;

    /**
     * List of formed bonds.<p>
     *
     * Can be thought of as an adjacency list in our graph representation of a chemical compound.
     */
    private final List<Bond> bonds = new ArrayList<>();

    /**
     * Current valence of the atom.<p>
     *
     * Initialized from {@code chemicalElement.normalValence}. Determines whether it is possible to add a bond.
     * A value of {@code 0} indicates that no more valence bonds can be formed.
     */
    private int valence;

    public Atom(ChemicalElement chemicalElement) {
        if (chemicalElement == null) {
            throw new IllegalArgumentException("Chemical element cannot be null");
        }
        this.id = ++Atom.counter;
        this.chemicalElement = chemicalElement;
        this.valence = chemicalElement.normalValence();
    }

    /**
     * Forms a valence bond between two atoms.<p>
     *
     * Decreases the valence of both atoms in case of successful bond creation.<p>
     *
     * Can be thought of as adding an undirected edge between two vertices
     * in our graph representation of a chemical compound.
     *
     * @param to atom to form the bond with
     * @param bondOrder order of the bond (e.g. {@code 2} for double bond)
     * @throws BondOrderExceedsValenceException if {@code bondOrder} is greater than {@code valence} of any atom
     * @throws BondAlreadyExistsException if bond between atoms exists
     */
    public void addBond(Atom to, int bondOrder) {
        if (to == null) {
            throw new IllegalArgumentException("Atom cannot be null.");
        }
        if (this.equals(to)) {
            throw new IllegalArgumentException("Atom cannot bond to an instance of itself.");
        }
        if (bondOrder < 1) {
            throw new IllegalArgumentException("Bond order cannot be less than 1.");
        }
        if (bondOrder > this.valence) {
            throw new BondOrderExceedsValenceException(bondOrder, this.valence, this);
        }
        if (bondOrder > to.valence) {
            throw new BondOrderExceedsValenceException(bondOrder, to.valence, to);
        }
        if (this.getBondedAtoms().contains(to)) {
            throw new BondAlreadyExistsException(this, to);
        }

        this.valence -= bondOrder;
        this.bonds.add(new Bond(this, to, bondOrder));
        to.valence -= bondOrder;
        to.bonds.add(new Bond(to, this, bondOrder));
    }

    public ChemicalElement getChemicalElement() {
        return this.chemicalElement;
    }

    /**
     * Returns a list of atoms this atom is bonded with.<p>
     *
     * Can be thought of as getting neighboring vertices in our graph representation of a chemical compound.
     *
     * @return a list of atoms bonded with this atom
     */
    public List<Atom> getBondedAtoms() {
        return this.bonds.stream().map(Bond::to).toList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Atom atom) {
            return this.id == atom.id;
        }
        return false;
    }

    /**
     * Representation of a bond.<p>
     * <p>
     * Serves as an edge in our graph representation of a chemical compound, connecting {@code Atom} vertices.
     */
    private record Bond(Atom from, Atom to, int bondOrder) {}
}