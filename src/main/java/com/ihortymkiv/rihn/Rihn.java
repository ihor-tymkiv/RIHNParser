package com.ihortymkiv.rihn;

import com.ihortymkiv.chemistry.Compound;

import java.util.List;

public final class Rihn {

    public static Compound getCompound(String input) {
        List<Token> tokens = new Lexer(input).scanTokens();
        Hydrocarbon hydrocarbon = new Parser(tokens).parse();
        new SemanticAnalyzer().analyze(hydrocarbon);
        return new CompoundGenerator().generateGraph(hydrocarbon);
    }

    private Rihn() {};
}
