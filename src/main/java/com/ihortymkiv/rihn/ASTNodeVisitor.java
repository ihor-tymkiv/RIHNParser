package com.ihortymkiv.rihn;

interface ASTNodeVisitor<R> {
    R visit(Compound compound);
    R visit(FunctionalGroup functionalGroup);
    R visit(Locants locants);
}
