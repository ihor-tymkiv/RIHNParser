package com.ihortymkiv.rihn;

import java.util.Objects;

/**
 * AST node representing a multiplying affix (e.g., "di", "tri")
 * and its corresponding integer value.
 */
public class MultiplyingAffix {
    MultiplyingAffix(Token token, int value) {
        Objects.requireNonNull(token, "Token must not be null");
        this.token = token;
        this.value = value;
    }
    final Token token;
    final int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiplyingAffix that)) return false;
        return value == that.value && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, value);
    }
}
