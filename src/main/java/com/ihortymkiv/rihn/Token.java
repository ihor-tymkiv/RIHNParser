package com.ihortymkiv.rihn;

import java.util.Objects;

/**
 * A record representing a lexical token.
 * This is the output of the {@link Lexer} and the input for the {@link Parser}.
 *
 * @param type The category of the token (e.g., DIGIT, WORD).
 * @param lexeme The actual string of text from the source.
 * @param position The character offset in the source string.
 */
record Token(TokenType type, String lexeme, int position) {
    Token {
        Objects.requireNonNull(type, "Type cannot be null.");
        Objects.requireNonNull(lexeme, "Lexeme cannot be null.");
    }
}
