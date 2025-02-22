package com.ihortymkiv.rihn;

import java.util.List;
import java.util.Objects;

class Locants extends ASTNode {
    Locants(List<Integer> locants) {
        Objects.requireNonNull(locants, "Locants cannot be null.");
        this.locants = List.copyOf(locants);
    }

    @Override
    <R> R accept(ASTNodeVisitor<R> visitor) { return visitor.visit(this); }

    final List<Integer> locants;
}
