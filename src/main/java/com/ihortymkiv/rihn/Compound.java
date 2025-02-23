package com.ihortymkiv.rihn;

import java.util.List;
import java.util.Objects;

class Compound extends ASTNode {
    Compound(boolean isCyclic, String stem, List<FunctionalGroup> groups) {
        Objects.requireNonNull(stem, "Stem cannot be null.");
        Objects.requireNonNull(groups, "Groups cannot be null.");
        this.isCyclic = isCyclic;
        this.stem = stem;
        this.groups = List.copyOf(groups);
    }

    @Override
    <R> R accept(ASTNodeVisitor<R> visitor) { return visitor.visit(this); }

    final boolean isCyclic;
    final String stem;
    final List<FunctionalGroup> groups;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compound compound)) return false;
        return isCyclic == compound.isCyclic &&
               Objects.equals(stem, compound.stem) &&
               Objects.equals(groups, compound.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCyclic, stem, groups);
    }
}