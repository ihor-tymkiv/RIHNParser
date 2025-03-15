package com.ihortymkiv.rihn;

import com.ihortymkiv.chemistry.Atom;
import com.ihortymkiv.chemistry.ChemicalElement;
import com.ihortymkiv.chemistry.Compound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CompoundGenerator implements Type.Visitor<Void> {
    private Hydrocarbon hydrocarbon;
    private Compound compound;
    private List<Integer> alkeneLocants = new ArrayList<>();
    private List<Integer> alkyneLocants = new ArrayList<>();

    Compound generateGraph(Hydrocarbon hydrocarbon) {
        this.hydrocarbon = hydrocarbon;

        compound = new Compound();
        hydrocarbon.type.accept(this);
        buildCarbonChain();
        fillWithHydrogen();

        return compound;
    }

    private void fillWithHydrogen() {
        Compound.BFS(compound.getAtoms().getFirst(), (atom) -> {
            for (int i = atom.getValence(); i > 0; i--) {
                Atom h = new Atom(ChemicalElement.Hydrogen);
                atom.addBond(h, 1);
                compound.addAtom(h);
            }
        });
    }

    private void buildCarbonChain() {
        Atom lastCarbon = null;
        for (int i = 0; i < hydrocarbon.stem.value; i++) {
            Atom c = new Atom(ChemicalElement.Carbon);
            compound.addAtom(c);
            if (Objects.nonNull(lastCarbon)) {
                int bondOrder = 1;
                if (alkeneLocants.contains(i)) {
                    bondOrder = 2;
                } else if (alkyneLocants.contains(i)) {
                    bondOrder = 3;
                }
                c.addBond(lastCarbon, bondOrder);
            }
            lastCarbon = c;
        }
        if (hydrocarbon.isCyclic) {
            lastCarbon.addBond(compound.getAtoms().getFirst(), 1);
        }
    }

    @Override
    public Void visit(Type.Alkane alkane) {
        return null;
    }

    @Override
    public Void visit(Type.Alkene alkene) {
        alkeneLocants = alkene.group.locants.locants;
        return null;
    }

    @Override
    public Void visit(Type.Alkyne alkyne) {
        alkyneLocants = alkyne.group.locants.locants;
        return null;
    }

    @Override
    public Void visit(Type.Enyne enyne) {
        enyne.alkene.accept(this);
        enyne.alkyne.accept(this);
        return null;
    }
}
