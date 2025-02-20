package com.ihortymkiv.rihn;

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
