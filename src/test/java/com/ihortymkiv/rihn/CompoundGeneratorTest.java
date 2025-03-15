package com.ihortymkiv.rihn;

import com.ihortymkiv.chemistry.Atom;
import com.ihortymkiv.chemistry.ChemicalElement;
import com.ihortymkiv.chemistry.Compound;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.ihortymkiv.rihn.TokenType.WORD;
import static org.junit.jupiter.api.Assertions.*;

class CompoundGeneratorTest {

    private Token word(String lexeme, int location) {
        return new Token(WORD, lexeme, location);
    }

    private void fillWithHydrogen(Compound compound) {
        Compound.BFS(compound.getAtoms().getFirst(), (atom) -> {
            for (int i = atom.getValence(); i > 0; i--) {
                Atom h = new Atom(ChemicalElement.Hydrogen);
                compound.addAtom(h);
                atom.addBond(h, 1);
            }
        });
    }

    /**
     * Simple check for graph nodes.
     */
    private void assertGraphEquality(Compound expected, Compound actual) {
        assertEquals(expected.getAtoms().size(), actual.getAtoms().size());
        for (int i = 0; i < expected.getAtoms().size(); i++) {
            Atom expectedAtom = expected.getAtoms().get(i);
            Atom actualAtom = actual.getAtoms().get(i);
            assertEquals(expectedAtom.getChemicalElement(), actualAtom.getChemicalElement());
            assertEquals(expectedAtom.getBonds().size(), actualAtom.getBonds().size());
            for (int j = 0; j < expectedAtom.getBonds().size(); j++) {
                Atom.Bond expectedBond = expectedAtom.getBonds().get(j);
                Atom.Bond actualBond = actualAtom.getBonds().get(j);
                assertEquals(expectedBond.to().getChemicalElement(), actualBond.to().getChemicalElement());
                assertEquals(expectedBond.bondOrder(), actualBond.bondOrder());
            }
        }
    }

    @Test
    void shouldGenerateSimpleCyclicHydrocarbon() {
        Hydrocarbon hydrocarbon = new Hydrocarbon(
                true,
                new Stem(word("prop", 5), 3),
                new Type.Alkane()
        );
        Compound actual = new CompoundGenerator().generateGraph(hydrocarbon);
        Compound expected = new Compound();
        Atom lastCarbon = null;
        for (int i = 0; i < 3; i++) {
            Atom c = new Atom(ChemicalElement.Carbon);
            expected.addAtom(c);
            if (Objects.nonNull(lastCarbon)) {
                c.addBond(lastCarbon, 1);
            }
            lastCarbon = c;
        }
        lastCarbon.addBond(expected.getAtoms().getFirst(), 1);
        fillWithHydrogen(expected);
        assertGraphEquality(expected, actual);
    }

    @Test
    void shouldGenerateCyclicHydrocarbonWithLocants() {
        Hydrocarbon hydrocarbon = new Hydrocarbon(
                true,
                new Stem(word("hex", 5), 6),
                new Type.Alkene(
                        new Group(
                                new Locants(List.of(1, 3, 5)),
                                new MultiplyingAffix(word("tri", 16), 3)
                        )
                )
        );
        Compound actual = new CompoundGenerator().generateGraph(hydrocarbon);
        Compound expected = new Compound();
        Atom lastCarbon = null;
        for (int i = 0; i < 6; i++) {
            Atom c = new Atom(ChemicalElement.Carbon);
            expected.addAtom(c);
            if (Objects.nonNull(lastCarbon)) {
                int bondOrder = 1;
                if (Set.of(1, 3, 5).contains(i)) {
                    bondOrder = 2;
                }
                c.addBond(lastCarbon, bondOrder);
            }
            lastCarbon = c;
        }
        lastCarbon.addBond(expected.getAtoms().getFirst(), 1);
        fillWithHydrogen(expected);
        assertGraphEquality(expected, actual);
    }

    @Test
    void shouldGenerateSimpleAlkyne() {
        Hydrocarbon hydrocarbon = new Hydrocarbon(
                false,
                new Stem(word("eth", 0), 2),
                new Type.Alkyne(
                        new Group(new Locants(List.of(1)), null)
                )
        );
        Compound actual = new CompoundGenerator().generateGraph(hydrocarbon);
        Compound expected = new Compound();
        Atom lastCarbon = null;
        for (int i = 0; i < 2; i++) {
            Atom c = new Atom(ChemicalElement.Carbon);
            expected.addAtom(c);
            if (Objects.nonNull(lastCarbon)) {
                c.addBond(lastCarbon, 3);
            }
            lastCarbon = c;
        }
        fillWithHydrogen(expected);
        assertGraphEquality(expected, actual);
    }

    @Test
    void shouldGenerateEnyne() {
        Hydrocarbon hydrocarbon = new Hydrocarbon(
                false,
                new Stem(word("hept", 0), 7),
                new Type.Enyne(
                        new Type.Alkene(
                                new Group(
                                        new Locants(List.of(1, 5)),
                                        new MultiplyingAffix(word("di", 10), 2)
                                )
                        ),
                        new Type.Alkyne(
                                new Group(
                                        new Locants(List.of(3)),
                                        null
                                )
                        )
                )
        );
        Compound actual = new CompoundGenerator().generateGraph(hydrocarbon);
        Compound expected = new Compound();
        Atom lastCarbon = null;
        for (int i = 0; i < 7; i++) {
            Atom c = new Atom(ChemicalElement.Carbon);
            expected.addAtom(c);
            if (Objects.nonNull(lastCarbon)) {
                int bondOrder = 1;
                if (Set.of(1, 5).contains(i)) {
                    bondOrder = 2;
                } else if (3 == i) {
                    bondOrder = 3;
                }
                c.addBond(lastCarbon, bondOrder);
            }
            lastCarbon = c;
        }
        fillWithHydrogen(expected);
        assertGraphEquality(expected, actual);
    }

}