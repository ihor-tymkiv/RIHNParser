package com.ihortymkiv.rihn;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void shouldParseSimpleCyclicCompound() {
        List<Token> tokens = new Lexer("cyclopropane").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                true, "prop", List.of(new FunctionalGroup(null, null, "an"))
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseCyclicCompoundWithLocants() {
        List<Token> tokens = new Lexer("cyclohexa-1,3,5-triene").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                true,
                "hex",
                List.of(new FunctionalGroup(new Locants(List.of(1, 3, 5)), "tri", "en"))
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseSimpleAlkyne() {
        List<Token> tokens = new Lexer("ethyne").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false, "eth", List.of(new FunctionalGroup(null, null, "yn"))
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseAlkeneWithSingleLocant() {
        List<Token> tokens = new Lexer("prop-1-ene").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false,
                "prop",
                List.of(new FunctionalGroup(new Locants(List.of(1)), null, "en"))
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseAlkeneWithLocants() {
        List<Token> tokens = new Lexer("propa-1,2-diene").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false,
                "prop",
                List.of(new FunctionalGroup(new Locants(List.of(1, 2)), "di", "en"))
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseCompoundWithDifferentBonds() {
        List<Token> tokens = new Lexer("hepta-1,5-dien-3-yne").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false,
                "hept",
                List.of(
                        new FunctionalGroup(new Locants(List.of(1, 5)), "di", "en"),
                        new FunctionalGroup(new Locants(List.of(3)), null, "yn")
                )
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldThrowForMissingStem() {
        List<Token> tokens = new Lexer("cycloyne").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Stem expected"));
        assertEquals("yn", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForMissingSuffix() {
        List<Token> tokens = new Lexer("cyclomethe").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Suffix expected"));
        assertEquals("e", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForCompoundWithConnectorButWithSimpleGroup() {
        List<Token> tokens = new Lexer("propa-5-ene").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Complex functional group with multiplying affix expected"));
        assertEquals("en", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForCompoundWithoutConnectorButWithComplexGroup() {
        List<Token> tokens = new Lexer("prop-5-diene").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Suffix expected"));
        assertEquals("e", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForCompoundWithInvalidEnding() {
        List<Token> tokens = new Lexer("prop-5-enero").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Ending expected"));
        assertEquals("", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForUnterminatedLocants() {
        List<Token> tokens = new Lexer("prop-5").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Unterminated locants."));
    }

    @Test
    void shouldThrowForMalformedLocants() {
        List<Token> tokens = new Lexer("prop-1,a-ene").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Digit expected"));
    }

}