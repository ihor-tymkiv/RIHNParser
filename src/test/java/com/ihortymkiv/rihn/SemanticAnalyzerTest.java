package com.ihortymkiv.rihn;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SemanticAnalyzerTest {

    private SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

    @Test
    void shouldThrowForInvalidCyclicCompound() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("cyclomethane"))
        );
        assertTrue(exception.getMessage().contains("Carbon chain length must be at least 3 for it to be cyclic"));
    }

    @Test
    void shouldThrowForInvalidLocant() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("pent-5-ene"))
        );
        assertTrue(exception.getMessage().contains("must be in range (0, 5)"));
    }

    @Test
    void shouldThrowForDuplicateLocant() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("penta-3,3-diene"))
        );
        assertTrue(exception.getMessage().contains("Locant 3 has already been specified"));
    }

    @Test
    void shouldThrowForInvalidOrder() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("penta-3,2-diene"))
        );
        assertTrue(exception.getMessage().contains("Locants must be in order of increasing value"));
    }

    @Test
    void shouldThrowForExceededValency() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("pent-2-en-3-yne"))
        );
        assertTrue(exception.getMessage().contains("Carbon #3 has exceeded available valency"));

        exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("pent-2-en-2-yne"))
        );
        assertTrue(exception.getMessage().contains("Carbon #2 has exceeded available valency"));
    }

    @Test
    void shouldThrowForSmallChainWithLocants() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("meth-1-yne"))
        );
        assertTrue(exception.getMessage().contains("Can't specify locants for a carbon count less than 2"));
    }

    @Test
    void shouldThrowIfSmallerSetFound() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("prop-2-ene"))
        );
        assertTrue(exception.getMessage().contains("Lowest set of locants rule violated, [2] could be [1]"));
    }

    @Test
    void shouldThrowForInvalidMultiplyingAffix() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("propa-1,2-triene"))
        );
        assertTrue(exception.getMessage().contains("Invalid multiplier (3) for number of locants (2)"));
    }

    @Test
    void shouldThrowForInvalidLocantsInEnyne() {
        SemanticAnalyzerException exception = assertThrows(
                SemanticAnalyzerException.class, () -> semanticAnalyzer.analyze(hydrocarbon("pent-4-en-1-yne"))
        );
        assertTrue(exception.getMessage().contains("Alkene locant (4) expected to be lower than alkyne's (1)."));
    }

    private Hydrocarbon hydrocarbon(String input) {
        return new Parser(new Lexer(input).scanTokens()).parse();
    }

}