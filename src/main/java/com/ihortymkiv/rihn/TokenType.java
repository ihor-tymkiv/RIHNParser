package com.ihortymkiv.rihn;

/**
 * Enumeration of all possible token types recognized by the {@link Lexer}.
 */
enum TokenType {
    // Single character tokens
    DIGIT,
    HYPHEN,
    COMMA,
    ENDING, // "e"

    // Multiple character tokens
    CYCLO, WORD,

    EOF
}
