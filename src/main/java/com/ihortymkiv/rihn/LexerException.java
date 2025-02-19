package com.ihortymkiv.rihn;

public class LexerException extends RuntimeException {
    private final String lexeme;
    private final int position;

    public LexerException(String message, String lexeme, int position) {
        super(String.format("%s '%s' at position %d.", message, lexeme, position));
        this.lexeme = lexeme;
        this.position = position;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getPosition() {
        return position;
    }
}
