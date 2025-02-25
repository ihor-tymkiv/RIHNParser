package com.ihortymkiv.rihn;

import java.util.List;
import java.util.Objects;

class Locants {
    Locants(List<Integer> locants) {
        Objects.requireNonNull(locants, "Locants cannot be null.");
        this.locants = List.copyOf(locants);
    }
    final List<Integer> locants;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Locants locants1)) return false;
        return Objects.equals(locants, locants1.locants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(locants);
    }
}
