package com.ihortymkiv.rihn;

import java.util.Objects;

abstract class Type {
    interface Visitor<R> {
        R visit(Alkane alkane);
        R visit(Alkene alkene);
        R visit(Alkyne alkyne);
        R visit(Enyne enyne);
    }
    abstract <R> R accept(Visitor<R> visitor);

    static class Alkane extends Type {
        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Alkane;
        }
    }

    static class Alkene extends Type {
        Alkene(Group group) {
            Objects.requireNonNull(group, "Group cannot be null.");
            this.group = group;
        }
        final Group group;

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Alkene alkene)) return false;
            return Objects.equals(group, alkene.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(group);
        }
    }

    static class Alkyne extends Type {
        Alkyne(Group group) {
            Objects.requireNonNull(group, "Group cannot be null.");
            this.group = group;
        }
        final Group group;

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Alkyne alkyne)) return false;
            return Objects.equals(group, alkyne.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(group);
        }
    }

    static class Enyne extends Type {
        Enyne(Alkene alkene, Alkyne alkyne) {
            Objects.requireNonNull(alkene, "Alkene cannot be null.");
            Objects.requireNonNull(alkyne, "Alkyne cannot be null.");
            this.alkene = alkene;
            this.alkyne = alkyne;
        }
        final Alkene alkene;
        final Alkyne alkyne;

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Enyne enyne)) return false;
            return Objects.equals(alkene, enyne.alkene) && Objects.equals(alkyne, enyne.alkyne);
        }

        @Override
        public int hashCode() {
            return Objects.hash(alkene, alkyne);
        }
    }
}
