package LexicalSeparator;

import java.util.List;

public class SyntaxAnalyzer {

    private List<Token> tokens;
    private int currentTokenIndex;

    public void analyze(List<Token> tokens) throws Exception {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        parseProgram();
    }

    private void parseProgram() throws Exception {
        while (currentTokenIndex < tokens.size()) {
            parseStatement();
            match(TokenType.SEMICOLON);
        }
    }

    private void parseStatement() throws Exception {
        TokenType currentTokenType = getCurrentTokenType();

        if (currentTokenType == TokenType.DATATYPE) {
            parseVariableDeclaration();
        } else if (currentTokenType == TokenType.IDENTIFIER) {
            parseAssignment();
        } else if (currentTokenType == TokenType.KEYWORD) {
            parseKeyword();
        } else {
            throw new Exception("Unexpected token: " + getCurrentToken());
        }
    }

    private void parseVariableDeclaration() throws Exception {
        match(TokenType.DATATYPE);
        parseVariableName();
        if (getCurrentTokenType() == TokenType.SYMBOL && getCurrentToken().equals("=")) {
            match(TokenType.SYMBOL);
            parseExpression();
        }
    }

    private void parseAssignment() throws Exception {
        parseVariableName();
        match(TokenType.SYMBOL);
        parseExpression();
    }

    private void parseVariableName() throws Exception {
        match(TokenType.IDENTIFIER);
    }

    private void parseKeyword() throws Exception {
        Token currentToken = getCurrentToken();

        if (currentToken.getValue().equals("tbh")) {
            match(TokenType.KEYWORD);
            match(TokenType.OPENPARENTHESIS);
            parseExpression();
            match(TokenType.CLOSEPARENTHESIS);
        } else if (currentToken.getValue().equals("g")) {
            match(TokenType.KEYWORD);
            match(TokenType.OPENPARENTHESIS);
            parseExpression();
            match(TokenType.CLOSEPARENTHESIS);
        } else if (currentToken.getValue().equals("forda")) {
            match(TokenType.KEYWORD);
            match(TokenType.OPENPARENTHESIS);
            parseExpression();
            match(TokenType.COMMA);
            parseExpression();
            match(TokenType.COMMA);
            parseExpression();
            match(TokenType.CLOSEPARENTHESIS);
        } else {
            throw new Exception("Unexpected keyword: " + getCurrentToken().getValue());
        }
    }

    private void parseExpression() throws Exception {
        TokenType currentTokenType = getCurrentTokenType();

        if (currentTokenType == TokenType.NUMBERS) {
            match(TokenType.NUMBERS);
        } else if (currentTokenType == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
        } else {
            throw new Exception("Unexpected token: " + getCurrentToken());
        }
    }

    private void match(TokenType expectedTokenType) throws Exception {
        if (getCurrentTokenType() != expectedTokenType) {
            throw new Exception("Expected token type " + expectedTokenType + ", but found " + getCurrentTokenType());
        }
        currentTokenIndex++;
    }

    private TokenType getCurrentTokenType() {
        return tokens.get(currentTokenIndex).getType();
    }

    private Token getCurrentToken() {
        return tokens.get(currentTokenIndex);
    }
}
