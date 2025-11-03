package com.ihortymkiv.rihn;

import com.ihortymkiv.chemistry.Compound;

import java.util.List;

/**
 * Public facade for the RIHNParser.
 * <p>
 * This class provides a simple, high-level interface to parse a hydrocarbon name
 * and generate its corresponding chemical compound graph.
 * <p>
 * It orchestrates the entire compiler pipeline:
 * 1. {@link Lexer}
 * 2. {@link Parser}
 * 3. {@link SemanticAnalyzer}
 * 4. {@link CompoundGenerator}
 */
public final class Rihn {

    public static Compound getCompound(String input) {
        List<Token> tokens = new Lexer(input).scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        new SemanticAnalyzer().analyze(hydrocarbon);
        return new CompoundGenerator().generateGraph(hydrocarbon);
    }

    private Rihn() {};
}
