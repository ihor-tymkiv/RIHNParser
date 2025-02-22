package com.ihortymkiv.rihn;

import java.util.List;
import java.util.Objects;

class Compound extends ASTNode {
    Compound(boolean isCyclic, String stem, boolean hasConnector, List<FunctionalGroup> functionalGroups) {
        Objects.requireNonNull(stem, "Stem cannot be null.");
        Objects.requireNonNull(functionalGroups, "FunctionalGroups cannot be null.");
        this.isCyclic = isCyclic;
        this.stem = stem;
        this.hasConnector = hasConnector;
        this.functionalGroups = List.copyOf(functionalGroups);
    }

    @Override
    <R> R accept(ASTNodeVisitor<R> visitor) { return visitor.visit(this); }

    final boolean isCyclic;
    final String stem;
    final boolean hasConnector;
    final List<FunctionalGroup> functionalGroups;
}