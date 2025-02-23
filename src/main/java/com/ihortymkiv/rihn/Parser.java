package com.ihortymkiv.rihn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static com.ihortymkiv.rihn.TokenType.*;

class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private String remainingWord = "";

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Compound parse() {
        return compound();
    }

    private Compound compound() {
        boolean isCyclic = match(CYCLO);
        String stem = stem();
        boolean hasConnector = connector();
        List<FunctionalGroup> groups = groups(hasConnector);
        consume(ENDING, "Ending expected");

        if (!isAtEnd()) {
            throw error("EOF expected after ending", peek());
        }

        return new Compound(isCyclic, stem, groups);
    }

    private String stem() {
        Token token = consume(WORD, "Keyword expected");
        String stem = extractKeywordFromWord(token.lexeme(), Keywords.STEMS.keySet());

        if (Objects.isNull(stem)) {
            throw error("Stem expected", token);
        }

        return stem;
    }

    private boolean connector() {
        Function<String, Boolean> checkCondition = s -> s.startsWith("a") && !s.startsWith("an");
        if (!remainingWord.isEmpty()) {
            if (checkCondition.apply(remainingWord)) {
                remainingWord = "";
                return true;
            }
            return false;
        }

        if (check(WORD)) {
            Token token = advance();
            if (checkCondition.apply(token.lexeme())) {
                remainingWord = token.lexeme().substring(1);
                return true;
            }
        }
        return false;
    }

    private List<FunctionalGroup> groups(boolean hasConnector) {
        List<FunctionalGroup> groups = new ArrayList<>();

        if (hasConnector) {
            groups.add(complexFunctionalGroup());
        } else {
            groups.add(simpleFunctionalGroup());
        }
        while (!isAtEnd() && !check(ENDING)) {
            groups.add(functionalGroup());
        }

        return groups;
    }

    private FunctionalGroup functionalGroup() {
        if (!remainingWord.isEmpty()) {
            String multiplyingAffix = multiplyingAffix();
            String suffix = suffix();

            return new FunctionalGroup(null, multiplyingAffix, suffix);
        }

        Locants locants = match(HYPHEN) ? locants() : null;
        String multiplyingAffix = multiplyingAffix();
        String suffix = suffix();

        return new FunctionalGroup(locants, multiplyingAffix, suffix);
    }

    private FunctionalGroup simpleFunctionalGroup() {
        Locants locants = match(HYPHEN) ? locants() : null;
        String suffix = suffix();
        return new FunctionalGroup(locants, null, suffix);
    }

    private FunctionalGroup complexFunctionalGroup() {
        Locants locants = match(HYPHEN) ? locants() : null;
        String multiplyingAffix = multiplyingAffix();
        String suffix = suffix();

        if (Objects.isNull(multiplyingAffix)) {
            throw error("Complex functional group with multiplying affix expected after connector 'a'", previous());
        }

        return new FunctionalGroup(locants, multiplyingAffix, suffix);
    }

    private String multiplyingAffix() {
        String word;
        if (!remainingWord.isEmpty()) {
            word = remainingWord;
        } else if (check(WORD)) {
            Token token = advance();
            word = token.lexeme();
        } else {
            return null;
        }
        return extractKeywordFromWord(word, Keywords.MULTIPLYING_AFFIXES.keySet());
    }

    private String suffix() {
        String word = null;
        if (!remainingWord.isEmpty()) {
            word = remainingWord;
        } else if (check(WORD)) {
            Token token = advance();
            word = token.lexeme();
        }
        String suffix = extractKeywordFromWord(word, Keywords.SUFFIXES.keySet());

        if (Objects.isNull(suffix)) {
            throw error("Suffix expected", peek());
        }

        return suffix;
    }

    private Locants locants() {
        List<Integer> locants = new ArrayList<>();
        Token token = consume(DIGIT, "Digit expected after hyphen");
        locants.add(Integer.parseInt(token.lexeme()));

        while (!isAtEnd() && !check(HYPHEN)) {
            consume(COMMA, "Comma expected after digit");
            token = consume(DIGIT, "Digit expected after comma");
            locants.add(Integer.parseInt(token.lexeme()));
        }

        consume(HYPHEN, "Unterminated locants. Expected hyphen");
        return new Locants(locants);
    }

    /**
     * Extracts a keyword from word, if successful stores the remaining string in remainingWord and returns the keyword,
     * otherwise returns null.
     * @param word string to extract from
     * @param keywords set of possible keywords
     * @return found keyword on success, otherwise null
     */
    private String extractKeywordFromWord(String word, Set<String> keywords) {
        if (Objects.isNull(word) || word.isEmpty()) {
            return null;
        }
        for (String keyword : keywords) {
            if (word.startsWith(keyword)) {
                remainingWord = word.substring(keyword.length());
                return keyword;
            }
        }
        remainingWord = word;
        return null;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(message, peek());
    }

    private ParserException error(String message, Token token) {
        return new ParserException(message, token);
    }

}
