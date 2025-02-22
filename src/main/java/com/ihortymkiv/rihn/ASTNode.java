package com.ihortymkiv.rihn;

abstract class ASTNode {
    abstract <R> R accept(ASTNodeVisitor<R> visitor);
}
