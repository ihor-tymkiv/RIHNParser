package com.ihortymkiv.rihn;

import java.util.ArrayList;
import java.util.List;

import static com.ihortymkiv.rihn.TokenType.*;

class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;

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
            case '1', '2', '3', '4', '5', '6', '7', '8', '9': addToken(DIGIT); break;
            case '-': addToken(HYPHEN); break;
            case ',': addToken(COMMA); break;

            default:
                if (source.startsWith("cyclo", start)) {
                    current += 4;
                    addToken(CYCLO);
                } else if (isAlpha(c)) {
                    consumeWord();
                } else {
                    throw error("Unexpected character");
                }
                break;
        }
    }

    private void consumeWord() {
        while (!isAtEnd() && isAlpha(peek())) consume();
        String lexeme = source.substring(start, current);
        if (lexeme.endsWith("e") && isAtEnd()) {
            addToken(WORD, lexeme.substring(0, lexeme.length() - 1), start);
            addToken(ENDING, "e", current - 1);
        } else {
            addToken(WORD, lexeme, start);
        }
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private char peek() {
        if (current >= source.length()) return '\0';
        return source.charAt(current);
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

    private void addToken(TokenType type, String lexeme, int position) {
        tokens.add(new Token(type, lexeme, position));
    }

    private LexerException error(String message) {
        String lexeme = source.substring(start, current);
        return new LexerException(message, lexeme, start);
    }

}
