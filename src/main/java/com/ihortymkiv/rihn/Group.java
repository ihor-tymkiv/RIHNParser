package com.ihortymkiv.rihn;

import java.util.Objects;

abstract class Group {
    interface Visitor<R> {
        R visit(Simple group);
        R visit(Complex group);
        R visit(Enyne group);
    }
    abstract <R> R accept(Visitor<R> visitor);

    Group(Locants locants) {
        Objects.requireNonNull(locants, "Locants cannot be null.");
        this.locants = locants;
    }
    final Locants locants;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group group)) return false;
        return Objects.equals(locants, group.locants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(locants);
    }

    static class Simple extends Group {
        Simple(Locants locants) {
            super(locants);
        }

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
    }

    static class Complex extends Group {
        Complex(Locants locants, MultiplyingAffix multiplyingAffix) {
            super(locants);
            Objects.requireNonNull(multiplyingAffix, "MultiplyingAffix cannot be null.");
            this.multiplyingAffix = multiplyingAffix;
        }
        final MultiplyingAffix multiplyingAffix;

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Complex complex)) return false;
            if (!super.equals(o)) return false;
            return Objects.equals(multiplyingAffix, complex.multiplyingAffix);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), multiplyingAffix);
        }
    }

    static class Enyne extends Group {
        Enyne(Locants locants, MultiplyingAffix multiplyingAffix) {
            super(locants);
            this.multiplyingAffix = multiplyingAffix;
        }
        final MultiplyingAffix multiplyingAffix;

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Enyne enyne)) return false;
            if (!super.equals(o)) return false;
            return Objects.equals(multiplyingAffix, enyne.multiplyingAffix);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), multiplyingAffix);
        }
    }
}
