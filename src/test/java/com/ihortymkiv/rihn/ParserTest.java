package com.ihortymkiv.rihn;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.ihortymkiv.rihn.TokenType.*;

class ParserTest {

    private Token word(String lexeme, int location) {
        return new Token(WORD, lexeme, location);
    }

    @Test
    void shouldParseSimpleCyclicCompound() {
        List<Token> tokens = new Lexer("cyclopropane").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                true,
                new Stem(word("prop", 5), 3),
                List.of(
                        new FunctionalGroup(
                                null, null, new Suffix(word("an", 9), 1)
                        )
                )
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseCyclicCompoundWithLocants() {
        List<Token> tokens = new Lexer("cyclohexa-1,3,5-triene").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                true,
                new Stem(word("hex", 5), 6),
                List.of(
                        new FunctionalGroup(
                                new Locants(List.of(1, 3, 5)),
                                new MultiplyingAffix(word("tri", 16), 3),
                                new Suffix(word("en", 19), 2)
                        )
                )
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseSimpleAlkyne() {
        List<Token> tokens = new Lexer("ethyne").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false,
                new Stem(word("eth", 0), 2),
                List.of(
                        new FunctionalGroup(
                                null,
                                null,
                                new Suffix(word("yn", 3), 3)
                        )
                )
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseAlkeneWithSingleLocant() {
        List<Token> tokens = new Lexer("prop-1-ene").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false,
                new Stem(word("prop", 0), 3),
                List.of(
                        new FunctionalGroup(
                                new Locants(List.of(1)),
                                null,
                                new Suffix(word("en", 7), 2)
                        )
                )
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseAlkeneWithLocants() {
        List<Token> tokens = new Lexer("propa-1,2-diene").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false,
                new Stem(word("prop", 0), 3),
                List.of(
                        new FunctionalGroup(
                                new Locants(
                                        List.of(1, 2)),
                                new MultiplyingAffix(word("di", 10), 2),
                                new Suffix(word("en", 12), 2)
                        )
                )
        );
        assertEquals(expected, compound);
    }

    @Test
    void shouldParseCompoundWithDifferentBonds() {
        List<Token> tokens = new Lexer("hepta-1,5-dien-3-yne").scanTokens();
        Compound compound = new Parser(tokens).parse();
        Compound expected = new Compound(
                false,
                new Stem(word("hept", 0), 7),
                List.of(
                        new FunctionalGroup(
                                new Locants(List.of(1, 5)),
                                new MultiplyingAffix(word("di", 10), 2),
                                new Suffix(word("en", 12), 2)
                        ),
                        new FunctionalGroup(
                                new Locants(List.of(3)),
                                null,
                                new Suffix(word("yn", 17), 3)
                        )
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
        List<Token> tokens = new Lexer("cyclomethne").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Suffix expected"));
        assertEquals("n", exception.getToken().lexeme());
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
        assertEquals("dien", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForCompoundWithInvalidEnding() {
        List<Token> tokens = new Lexer("prop-5-en").scanTokens();
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