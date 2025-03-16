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
    void shouldParseSimpleCyclicHydrocarbon() {
        List<Token> tokens = new Lexer("cyclopropane").scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        Hydrocarbon expected = new Hydrocarbon(
                true,
                new Stem(word("prop", 5), 3),
                new Type.Alkane()
        );
        assertEquals(expected, hydrocarbon);
    }

    @Test
    void shouldParseCyclicHydrocarbonWithLocants() {
        List<Token> tokens = new Lexer("cyclohexa-1,3,5-triene").scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        Hydrocarbon expected = new Hydrocarbon(
                true,
                new Stem(word("hex", 5), 6),
                new Type.Alkene(
                        new Group(
                                new Locants(List.of(1, 3, 5)),
                                new MultiplyingAffix(word("tri", 16), 3)
                        )
                )
        );
        assertEquals(expected, hydrocarbon);
    }

    @Test
    void shouldParseSimpleAlkyne() {
        List<Token> tokens = new Lexer("eth-1-yne").scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        Hydrocarbon expected = new Hydrocarbon(
                false,
                new Stem(word("eth", 0), 2),
                new Type.Alkyne(
                        new Group(new Locants(List.of(1)), null)
                )
        );
        assertEquals(expected, hydrocarbon);
    }

    @Test
    void shouldParseAlkeneWithSingleLocant() {
        List<Token> tokens = new Lexer("prop-1-ene").scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        Hydrocarbon expected = new Hydrocarbon(
                false,
                new Stem(word("prop", 0), 3),
                new Type.Alkene(
                        new Group(new Locants(List.of(1)), null)
                )
        );
        assertEquals(expected, hydrocarbon);
    }

    @Test
    void shouldParseAlkeneWithLocants() {
        List<Token> tokens = new Lexer("propa-1,2-diene").scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        Hydrocarbon expected = new Hydrocarbon(
                false,
                new Stem(word("prop", 0), 3),
                new Type.Alkene(
                        new Group(
                                new Locants(List.of(1, 2)),
                                new MultiplyingAffix(word("di", 10), 2)
                        )
                )
        );
        assertEquals(expected, hydrocarbon);
    }

    @Test
    void shouldParseEnyne() {
        List<Token> tokens = new Lexer("hepta-1,5-dien-3-yne").scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        Hydrocarbon expected = new Hydrocarbon(
                false,
                new Stem(word("hept", 0), 7),
                new Type.Enyne(
                        new Type.Alkene(
                                new Group(
                                        new Locants(List.of(1, 5)),
                                        new MultiplyingAffix(word("di", 10), 2)
                                )
                        ),
                        new Type.Alkyne(
                                new Group(
                                        new Locants(List.of(3)),
                                        null
                                )
                        )
                )
        );
        assertEquals(expected, hydrocarbon);
    }

    @Test
    void shouldThrowForInvalidEnyne() {
        List<Token> tokens = new Lexer("hepta-1,5-diyn-3-ene").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Ending expected"));
        assertEquals("-", exception.getToken().lexeme());
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
        assertTrue(exception.getMessage().contains("Hyphen expected"));
        assertEquals("n", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForHydrocarbonWithConnectorButWithSimpleGroup() {
        List<Token> tokens = new Lexer("propa-5-ene").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Complex group with multiplying affix expected"));
        assertEquals("-", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForHydrocarbonWithoutConnectorButWithComplexGroup() {
        List<Token> tokens = new Lexer("prop-5-diene").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Suffix 'en' or 'yn' expected"));
        assertEquals("dien", exception.getToken().lexeme());
    }

    @Test
    void shouldThrowForHydrocarbonWithInvalidEnding() {
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

    @Test
    void shouldThrowForMultipleLocantsWhenSingleExpected() {
        List<Token> tokens = new Lexer("dec-1,2,3,4,5,6,7,8,9-ene").scanTokens();
        ParserException exception = assertThrows(ParserException.class, () -> new Parser(tokens).parse());
        assertTrue(exception.getMessage().contains("Only one locant expected"));
    }

}