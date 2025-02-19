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
                new Token(STEM, "prop", 5),
                new Token(SUFFIX, "an", 9),
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
                new Token(STEM, "hex", 5),
                new Token(CONNECTOR, "a", 8),
                new Token(LOCANTS, "-1,3,5-", 9),
                new Token(MULTIPLYING_AFFIX, "tri", 16),
                new Token(SUFFIX, "en", 19),
                new Token(ENDING, "e", 21),
                new Token(EOF, "", 22)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeSimpleAlkyne() {
        List<Token> tokens = new Lexer("ethyne").scanTokens();
        List<Token> expected = List.of(
                new Token(STEM, "eth", 0),
                new Token(SUFFIX, "yn", 3),
                new Token(ENDING, "e", 5),
                new Token(EOF, "", 6)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeAlkeneWithSingleLocant() {
        List<Token> tokens = new Lexer("prop-1-ene").scanTokens();
        List<Token> expected = List.of(
                new Token(STEM, "prop", 0),
                new Token(LOCANTS, "-1-", 4),
                new Token(SUFFIX, "en", 7),
                new Token(ENDING, "e", 9),
                new Token(EOF, "", 10)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeAlkeneWithLocants() {
        List<Token> tokens = new Lexer("propa-1,2-diene").scanTokens();
        List<Token> expected = List.of(
                new Token(STEM, "prop", 0),
                new Token(CONNECTOR, "a", 4),
                new Token(LOCANTS, "-1,2-", 5),
                new Token(MULTIPLYING_AFFIX, "di", 10),
                new Token(SUFFIX, "en", 12),
                new Token(ENDING, "e", 14),
                new Token(EOF, "", 15)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldTokenizeCompoundWithDifferentBonds() {
        List<Token> tokens = new Lexer("hepta-1,5-dien-3-yne").scanTokens();
        List<Token> expected = List.of(
                new Token(STEM, "hept", 0),
                new Token(CONNECTOR, "a", 4),
                new Token(LOCANTS, "-1,5-", 5),
                new Token(MULTIPLYING_AFFIX, "di", 10),
                new Token(SUFFIX, "en", 12),
                new Token(LOCANTS, "-3-", 14),
                new Token(SUFFIX, "yn", 17),
                new Token(ENDING, "e", 19),
                new Token(EOF, "", 20)
        );
        assertIterableEquals(expected, tokens);
    }

    @Test
    void shouldThrowForUnterminatedLocants() {
        LexerException exception = assertThrowsExactly(
                LexerException.class,
                () -> new Lexer("buta-1,2").scanTokens()
        );
        assertTrue(exception.getMessage().contains("Unterminated locants"));
        assertEquals("-1,2", exception.getLexeme());
        assertEquals(4, exception.getPosition());
    }

    @Test
    void shouldThrowForInvalidFormLocants() {
        LexerException exception = assertThrowsExactly(
                LexerException.class,
                () -> new Lexer("buta-1,a,b,c,02-ene").scanTokens()
        );
        assertTrue(exception.getMessage().contains("Invalid locants form"));
        assertEquals("-1,a", exception.getLexeme());
        assertEquals(4, exception.getPosition());
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

    @Test
    void shouldThrowForKeywordNotFound() {
        LexerException exception = assertThrowsExactly(
                LexerException.class,
                () -> new Lexer("tri-1-yne").scanTokens()
        );
        assertTrue(exception.getMessage().contains("Couldn't find keyword of type STEM"));
        assertEquals("tri-1", exception.getLexeme());
        assertEquals(0, exception.getPosition());
    }
}
