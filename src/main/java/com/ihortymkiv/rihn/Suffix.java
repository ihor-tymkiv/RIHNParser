package com.ihortymkiv.rihn;

import java.util.Objects;

public class Suffix {
    Suffix(Token token, int bondOrder) {
        Objects.requireNonNull(token, "Token cannot be null");
        this.token = token;
        this.bondOrder = bondOrder;
    }
    final Token token;
    final int bondOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Suffix suffix)) return false;
        return bondOrder == suffix.bondOrder && Objects.equals(token, suffix.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, bondOrder);
    }
}
