package com.ihortymkiv.rihn;

interface ASTNodeVisitor<R> {
    R visit(Compound compound);
    R visit(Stem stem);
    R visit(FunctionalGroup functionalGroup);
    R visit(MultiplyingAffix multiplyingAffix);
    R visit(Suffix suffix);
    R visit(Locants locants);
}
