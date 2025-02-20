package com.ihortymkiv.rihn;

import java.util.Objects;

record Token(TokenType type, String lexeme, int position) {
    Token {
        Objects.requireNonNull(type, "Type cannot be null.");
        Objects.requireNonNull(lexeme, "Lexeme cannot be null.");
    }
}
