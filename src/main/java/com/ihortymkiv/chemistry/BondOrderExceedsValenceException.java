package com.ihortymkiv.chemistry;

/**
 * Thrown when the valence of an atom is less than required to form a bond.
 */
public class BondOrderExceedsValenceException extends RuntimeException {
    public BondOrderExceedsValenceException(int bondOrder, int currentValence, Atom atom) {
        super(String.format("Bond order %d exceeds available valence (%d) for %s.", bondOrder, currentValence, atom));
    }
}
