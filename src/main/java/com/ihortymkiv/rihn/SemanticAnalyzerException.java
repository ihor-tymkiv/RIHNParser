package com.ihortymkiv.rihn;

class SemanticAnalyzerException extends RuntimeException {
    private final Token token;

    SemanticAnalyzerException(String message) {
        super(String.format("%s.", message));
        this.token = null;
    }

    SemanticAnalyzerException(String message, Token token) {
        super(String.format("%s '%s' at position %d.", message, token.lexeme(), token.position()));
        this.token = token;
    }

    Token getToken() {
        return token;
    }
}
