package com.ihortymkiv.chemistry;

public class BondAlreadyExistsException extends RuntimeException {
    public BondAlreadyExistsException(Atom from, Atom to) {
        super(String.format("Bond between %s and %s already exists.", from, to));
    }
}
