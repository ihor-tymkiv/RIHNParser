package com.ihortymkiv.rihn;

import java.util.Objects;

class Hydrocarbon {
    Hydrocarbon(boolean isCyclic, Stem stem, Type type) {
        Objects.requireNonNull(stem, "Stem cannot be null.");
        Objects.requireNonNull(type, "Type cannot be null.");
        this.isCyclic = isCyclic;
        this.stem = stem;
        this.type = type;
    }
    final boolean isCyclic;
    final Stem stem;
    final Type type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hydrocarbon that)) return false;
        return isCyclic == that.isCyclic && Objects.equals(stem, that.stem) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCyclic, stem, type);
    }
}