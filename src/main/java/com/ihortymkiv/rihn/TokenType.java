package com.ihortymkiv.rihn;

enum TokenType {
    // Single character tokens
    CONNECTOR, // "a"
    ENDING, // "e"

    // Multiple character tokens
    CYCLO, STEM, LOCANTS, MULTIPLYING_AFFIX, SUFFIX,

    EOF
}
