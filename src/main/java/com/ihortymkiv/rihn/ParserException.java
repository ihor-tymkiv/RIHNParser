package com.ihortymkiv.rihn;

class ParserException extends RuntimeException {
    private final Token token;

    ParserException(String message, Token token) {
        super(String.format("%s '%s' at position %d.", message, token.lexeme(), token.position()));
        this.token = token;
    }

    Token getToken() {
        return token;
    }
}
