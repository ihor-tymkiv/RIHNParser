package com.ihortymkiv.rihn;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.ihortymkiv.rihn.TokenType.*;

class LexerTest {

    @Test
    void shouldTokenizeSimpleCyclicCompound() {
        List<Token> tokens = new Lexer("cyclopropane").scanTokens();
        List<Token> expected = List.of(
                new Token(CYCLO, "cyclo", 0),
                new Token(WORD, "propan", 5),
                new Token(ENDING, "e", 11),
                new Token(EOF, "", 12)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeCyclicCompoundWithLocants() {
        List<Token> tokens = new Lexer("cyclohexa-1,3,5-triene").scanTokens();
        List<Token> expected = List.of(
                new Token(CYCLO, "cyclo", 0),
                new Token(WORD, "hexa", 5),
                new Token(HYPHEN, "-", 9),
                new Token(DIGIT, "1", 10),
                new Token(COMMA, ",", 11),
                new Token(DIGIT, "3", 12),
                new Token(COMMA, ",", 13),
                new Token(DIGIT, "5", 14),
                new Token(HYPHEN, "-", 15),
                new Token(WORD, "trien", 16),
                new Token(ENDING, "e", 21),
                new Token(EOF, "", 22)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeSimpleAlkyne() {
        List<Token> tokens = new Lexer("ethyne").scanTokens();
        List<Token> expected = List.of(
                new Token(WORD, "ethyn", 0),
                new Token(ENDING, "e", 5),
                new Token(EOF, "", 6)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeAlkeneWithSingleLocant() {
        List<Token> tokens = new Lexer("prop-1-ene").scanTokens();
        List<Token> expected = List.of(
                new Token(WORD, "prop", 0),
                new Token(HYPHEN, "-", 4),
                new Token(DIGIT, "1", 5),
                new Token(HYPHEN, "-", 6),
                new Token(WORD, "en", 7),
                new Token(ENDING, "e", 9),
                new Token(EOF, "", 10)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeAlkeneWithLocants() {
        List<Token> tokens = new Lexer("propa-1,2-diene").scanTokens();
        List<Token> expected = List.of(
                new Token(WORD, "propa", 0),
                new Token(HYPHEN, "-", 5),
                new Token(DIGIT, "1", 6),
                new Token(COMMA, ",", 7),
                new Token(DIGIT, "2", 8),
                new Token(HYPHEN, "-", 9),
                new Token(WORD, "dien", 10),
                new Token(ENDING, "e", 14),
                new Token(EOF, "", 15)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeCompoundWithDifferentBonds() {
        List<Token> tokens = new Lexer("hepta-1,5-dien-3-yne").scanTokens();
        List<Token> expected = List.of(
                new Token(WORD, "hepta", 0),
                new Token(HYPHEN, "-", 5),
                new Token(DIGIT, "1", 6),
                new Token(COMMA, ",", 7),
                new Token(DIGIT, "5", 8),
                new Token(HYPHEN, "-", 9),
                new Token(WORD, "dien", 10),
                new Token(HYPHEN, "-", 14),
                new Token(DIGIT, "3", 15),
                new Token(HYPHEN, "-", 16),
                new Token(WORD, "yn", 17),
                new Token(ENDING, "e", 19),
                new Token(EOF, "", 20)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldThrowForUnexpectedCharacter() {
        LexerException exception = assertThrowsExactly(
                LexerException.class,
                () -> new Lexer("pent+ne").scanTokens()
        );
        assertTrue(exception.getMessage().contains("Unexpected character"));
        assertEquals("+", exception.getLexeme());
        assertEquals(4, exception.getPosition());
    }

}
