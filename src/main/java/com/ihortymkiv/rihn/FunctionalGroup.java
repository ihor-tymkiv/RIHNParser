package com.ihortymkiv.rihn;

import java.util.Objects;

class FunctionalGroup extends ASTNode {
    FunctionalGroup(Locants locants, MultiplyingAffix multiplyingAffix, Suffix suffix) {
        Objects.requireNonNull(suffix, "Suffix cannot be null.");
        this.locants = locants;
        this.multiplyingAffix = multiplyingAffix;
        this.suffix = suffix;
    }

    @Override
    <R> R accept(ASTNodeVisitor<R> visitor) { return visitor.visit(this); }

    final Locants locants;
    final MultiplyingAffix multiplyingAffix;
    final Suffix suffix;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalGroup functionalGroup)) return false;
        return Objects.equals(locants, functionalGroup.locants) &&
               Objects.equals(multiplyingAffix, functionalGroup.multiplyingAffix) &&
               Objects.equals(suffix, functionalGroup.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locants, multiplyingAffix, suffix);
    }
}
