package com.ihortymkiv.rihn;

import java.util.Objects;

class Stem extends ASTNode {
    Stem(Token token, int value) {
        Objects.requireNonNull(token, "Token cannot be null.");
        this.token = token;
        this.value = value;
    }

    @Override
    <R> R accept(ASTNodeVisitor<R> visitor) { return visitor.visit(this); }

    final Token token;
    final int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stem stem)) return false;
        return value == stem.value && Objects.equals(token, stem.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, value);
    }
}
