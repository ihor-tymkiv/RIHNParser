package com.ihortymkiv.chemistry;

/**
 * Enumeration of chemical elements with their symbol and normal valence.
 */
public enum ChemicalElement {
    Hydrogen("H", 1),
    Carbon("C", 4);

    private final String symbol;
    private final int normalValence;

    ChemicalElement(String symbol, int normalValence) {
        this.symbol = symbol;
        this.normalValence = normalValence;
    }

    public String symbol() {
        return this.symbol;
    }

    public int normalValence() {
        return this.normalValence;
    }
}
