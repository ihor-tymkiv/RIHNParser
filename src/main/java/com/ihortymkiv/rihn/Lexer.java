package com.ihortymkiv.rihn;

import java.util.ArrayList;
import java.util.List;

import static com.ihortymkiv.rihn.TokenType.*;

class Lexer {
    private static final List<String> stems;
    private static final List<String> multiplying_affixes;
    private static final int STEM_MAX_LENGTH = 4;
    private static final int MULTIPLYING_AFFIX_MAX_LENGTH = 5;
    static {
        stems = List.of("meth", "eth", "prop", "but", "pent", "hex", "hept", "oct", "non", "dec");
        multiplying_affixes = List.of("di", "tri", "tetra", "penta", "hexa", "hepta", "octa", "nona");
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private boolean stemSeen = false;

    Lexer(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", current));
        return tokens;
    }

    private void scanToken() {
        char c = consume();

        switch (c) {
            case '-':
                consumeLocants();
                break;

            case 'y':
                if (match('n')) {
                    addToken(SUFFIX); // "yn"
                }
                break;

            case 'a':
                if (peek() != 'n' || peek(1) == 'o') {
                    // 'a' is not part of SUFFIX "an" or is followed by MULTIPLYING_AFFIX "nona".
                    addToken(CONNECTOR);
                } else {
                    consume();
                    addToken(SUFFIX); // "an"
                }
                break;

            case 'e':
                if (match('n')) {
                    addToken(SUFFIX); // "en"
                } else if (peek() == '\0') {
                    addToken(ENDING); // "e"
                } else {
                    consumeKeywordOfType(STEM, stems, STEM_MAX_LENGTH);
                }
                break;


            default:
                if (c == 'c' && source.startsWith("cyclo", start)) {
                    for (int i = 0; i < 4; i++) consume();
                    addToken(CYCLO);
                } else if (isAlpha(c)) {
                    consumeKeyword();
                } else {
                    throw error("Unexpected character");
                }
                break;
        }
    }

    private void consumeLocants() {
        while (peek() != '-' && !isAtEnd()) {
            char c = consume();
            if (!isNonzeroDigit(c) && c != ',') throw error("Invalid locants form");
        }

        if (isAtEnd()) {
            throw error("Unterminated locants");
        }

        consume();
        addToken(LOCANTS);
    }

    private void consumeKeyword() {
        if (!stemSeen) {
            consumeKeywordOfType(STEM, stems, STEM_MAX_LENGTH);
            stemSeen = true;
        } else {
            consumeKeywordOfType(MULTIPLYING_AFFIX, multiplying_affixes, MULTIPLYING_AFFIX_MAX_LENGTH);
        }
    }

    private void consumeKeywordOfType(TokenType type, List<String> keywords, int max_length) {
        boolean keywordFound = false;
        while (true) {
            keywordFound = keywords.contains(source.substring(start, current));
            if (getOffset() > max_length || keywordFound) {
                break;
            }
            consume();
        }

        if (!keywordFound) {
            throw error("Couldn't find keyword of type " + type.name());
        }

        addToken(type);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isNonzeroDigit(char c) {
        return (c >= '1' && c <= '9');
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private int getOffset() {
        return this.current - this.start;
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int offset) {
        if (current + offset >= source.length()) return '\0';
        return source.charAt(current + offset);
    }

    private char consume() {
        if (isAtEnd()) return '\0';
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType type) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, start));
    }

    private LexerException error(String message) {
        String lexeme = source.substring(start, current);
        return new LexerException(message, lexeme, start);
    }

}
