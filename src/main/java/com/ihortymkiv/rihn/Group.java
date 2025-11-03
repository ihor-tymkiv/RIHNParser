package com.ihortymkiv.rihn;

import java.util.Objects;

/**
 * AST node representing a functional group, combining locants
 * and an optional multiplying affix (e.g., "-1,2-di").
 */
class Group {
    Group(Locants locants, MultiplyingAffix multiplyingAffix) {
        Objects.requireNonNull(locants, "Locants cannot be null.");
        this.locants = locants;
        this.multiplyingAffix = multiplyingAffix;
    }
    final Locants locants;
    final MultiplyingAffix multiplyingAffix;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group group)) return false;
        return Objects.equals(locants, group.locants) && Objects.equals(multiplyingAffix, group.multiplyingAffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locants, multiplyingAffix);
    }
}
