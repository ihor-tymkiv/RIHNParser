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

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Hydrocarbon parse() {
        return hydrocarbon();
    }

    private Hydrocarbon hydrocarbon() {
        boolean isCyclic = match(CYCLO);
        Stem stem = stem();
        Type type = type();
        consume(ENDING, "Ending expected");

        if (!isAtEnd()) {
            throw error("EOF expected after ending", peek());
        }

        return new Hydrocarbon(isCyclic, stem, type);
    }

    private Stem stem() {
        Token token = consume(WORD, "Keyword expected");
        Token stem = extractKeywordFromWord(token, Keywords.STEMS.keySet());

        if (Objects.isNull(stem)) {
            throw error("Stem expected", token);
        }

        return new Stem(previous(), Keywords.STEMS.get(stem.lexeme()));
    }

    private boolean connector() {
        Function<String, Boolean> checkCondition = s -> s.startsWith("a") && !s.startsWith("an");

        if (!check(WORD)) {
            return false;
        }

        Token token = advance();
        if (checkCondition.apply(token.lexeme())) {
            String remainingWord = token.lexeme().substring(1);
            tokens.set(current - 1, new Token(WORD, "a", token.position()));
            if (!remainingWord.isEmpty()) {
                tokens.add(current, new Token(WORD, remainingWord, token.position() + 1));
            }
            return true;
        }
        
        current--;
        return false;
    }

    private Type type() {
        if (check(WORD)) {
            Token token = advance();
            Token suffix = extractKeywordFromWord(token, Set.of("an"));
            if (Objects.nonNull(suffix)) {
                return new Type.Alkane();
            }
        }

        Group group = connector() ? complexGroup() : simpleGroup();
        Token token = consume(WORD, "Keyword expected");
        Token firstSuffix = extractKeywordFromWord(token, Set.of("en", "yn"));
        if (Objects.isNull(firstSuffix)) {
            throw error("Suffix 'en' or 'yn' expected", token);
        }

        if (firstSuffix.lexeme().equals("en")) {
            if (!check(HYPHEN)) {
                return new Type.Alkene(group);
            }

            Group secondGroup = enyneGroup();
            token = consume(WORD, "Keyword expected");
            Token secondSuffix = extractKeywordFromWord(token, Set.of("yn"));

            if (Objects.isNull(secondSuffix)) {
                throw error("Suffix 'yn' expected", token);
            }

            return new Type.Enyne(new Type.Alkene(group), new Type.Alkyne(secondGroup));
        }
        return new Type.Alkyne(group);
    }

    private Group enyneGroup() {
        Locants locants = locants();
        MultiplyingAffix multiplyingAffix = multiplyingAffix();
        return new Group.Enyne(locants, multiplyingAffix);
    }

    private Group complexGroup() {
        Locants locants = locants();
        MultiplyingAffix multiplyingAffix = multiplyingAffix();

        if (Objects.isNull(multiplyingAffix)) {
            throw error("Complex group with multiplying affix expected after connector 'a'", previous());
        }

        return new Group.Complex(locants, multiplyingAffix);
    }

    private Group simpleGroup() {
        Locants locants = locants();
        return new Group.Simple(locants);
    }

    private MultiplyingAffix multiplyingAffix() {
        if (check(WORD)) {
            Token token = advance();
            Token multiplyingAffix = extractKeywordFromWord(token, Keywords.MULTIPLYING_AFFIXES.keySet());
            if (Objects.nonNull(multiplyingAffix)) {
                return new MultiplyingAffix(
                        multiplyingAffix, Keywords.MULTIPLYING_AFFIXES.get(multiplyingAffix.lexeme())
                );
            }
        }
        return null;
    }

    private Locants locants() {
        consume(HYPHEN, "Hyphen expected");
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
     * Extracts a keyword from token, if successful returns a token including the keyword, otherwise returns null.
     * @param token token to extract from
     * @param keywords set of possible keywords
     * @return token with keyword as lexeme on success, otherwise null
     */
    private Token extractKeywordFromWord(Token token, Set<String> keywords) {
        if (Objects.isNull(token) || token.lexeme().isEmpty()) {
            return null;
        }
        for (String keyword : keywords) {
            if (token.lexeme().startsWith(keyword)) {
                String remainingWord = token.lexeme().substring(keyword.length());
                Token newPrevious = new Token(WORD, keyword, token.position());
                tokens.set(current - 1, newPrevious);
                if (!remainingWord.isEmpty()) {
                    tokens.add(current, new Token(WORD, remainingWord, newPrevious.position() + keyword.length()));
                }
                return newPrevious;
            }
        }
        // If nothing found, go back to let the next call process the unprocessed WORD.
        current--;
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
