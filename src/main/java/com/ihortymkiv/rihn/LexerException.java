package com.ihortymkiv.rihn;

class LexerException extends RuntimeException {
    private final String lexeme;
    private final int position;

    LexerException(String message, String lexeme, int position) {
        super(String.format("%s '%s' at position %d.", message, lexeme, position));
        this.lexeme = lexeme;
        this.position = position;
    }

    String getLexeme() {
        return lexeme;
    }

    int getPosition() {
        return position;
    }
}
