package com.ihortymkiv.chemistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtomTest {

    private Atom atom;

    @BeforeEach
    void setUp() {
        this.atom = new Atom(ChemicalElement.Carbon);
    }

    @Test
    void shouldSuccessfullyAddBonds() {
        atom.addBond(new Atom(ChemicalElement.Carbon), 2);
        atom.addBond(new Atom(ChemicalElement.Carbon), 2);

        assertEquals(2, atom.getBondedAtoms().size());
    }

    @Test
    void shouldNotAddIllegalBonds() {
        assertThrows(IllegalArgumentException.class, () -> atom.addBond(new Atom(ChemicalElement.Hydrogen), 0));
    }

    @Test
    void shouldNotAddLoopBond() {
        assertThrows(IllegalArgumentException.class, () -> atom.addBond(atom, 1));
    }

    @Test
    void shouldNotAddExistingBond() {
        Atom atom2 = new Atom(ChemicalElement.Carbon);
        atom.addBond(atom2, 1);

        assertThrows(BondAlreadyExistsException.class, () -> atom2.addBond(atom, 1));
        assertThrows(BondAlreadyExistsException.class, () -> atom.addBond(atom2, 1));
    }

    @Test
    void shouldNotAddBondIfBondOrderExceedsValence() {
        assertThrows(BondOrderExceedsValenceException.class, () -> atom.addBond(new Atom(ChemicalElement.Carbon), 5));
    }

}