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
        } else if (currentTokenType == TokenType.NUMBERS) {
            parseArithOp();
        } else if (currentTokenType == TokenType.STRING_LITERAL) {
            parseAssignOp();
        } else {
            throw new Exception("Unexpected token: " + getCurrentToken());
        }
    }

    private void parseVariableDeclaration() throws Exception {
        if (getCurrentTokenInput().equals("yarn")) {
            match(TokenType.DATATYPE);
            if (getCurrentTokenType() == TokenType.OPENBRACKET) {
                match(TokenType.OPENBRACKET);
                match(TokenType.CLOSEBRACKET);
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.OPENBRACE);
                    match(TokenType.STRING_LITERAL);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            match(TokenType.COMMA);
                            match(TokenType.STRING_LITERAL);
                        } while (getCurrentTokenType() == TokenType.COMMA);
                    }
                    match(TokenType.CLOSEBRACE);
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.STRING_LITERAL);
                    while (getCurrentTokenInput().equals("lahamz")) {
                        match(TokenType.ARITH_OPERATOR);
                        match(TokenType.STRING_LITERAL);
                        System.out.println(getCurrentTokenType());
                    }
                }
            }

        } else if (getCurrentTokenInput().equals("digits")) {
            match(TokenType.DATATYPE);
            if (getCurrentTokenType() == TokenType.OPENBRACKET) {
                match(TokenType.OPENBRACKET);
                match(TokenType.CLOSEBRACKET);
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.OPENBRACE);
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            match(TokenType.COMMA);
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.COMMA);
                    }
                    match(TokenType.CLOSEBRACE);
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                        do {
                            match(TokenType.ARITH_OPERATOR);
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
                    }
                }
            }
        } else if (getCurrentTokenInput().equals("lutang")) {
            match(TokenType.DATATYPE);
            if (getCurrentTokenType() == TokenType.OPENBRACKET) {
                match(TokenType.OPENBRACKET);
                match(TokenType.CLOSEBRACKET);
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.OPENBRACE);
                    match(TokenType.NUMBERS);
                    match(TokenType.POINT);
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            match(TokenType.COMMA);
                            match(TokenType.NUMBERS);
                            match(TokenType.POINT);
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.COMMA);
                    }
                    match(TokenType.CLOSEBRACE);
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.NUMBERS);
                    match(TokenType.POINT);
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                        do {
                            match(TokenType.ARITH_OPERATOR);
                            match(TokenType.NUMBERS);
                            match(TokenType.POINT);
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
                    }
                }
            }
        }
    }

    private void parseArithOp() throws Exception {
        TokenType currentTokenType = getCurrentTokenType();

        if (currentTokenType == TokenType.NUMBERS) {
            match(TokenType.NUMBERS);
            match(TokenType.ARITH_OPERATOR);
            match(TokenType.NUMBERS);
        } else {
            throw new Exception("Unexpected keyword: " + getCurrentToken().getValue());
        }
    }

    private void parseAssignOp() throws Exception {
        TokenType currentTokenType = getCurrentTokenType();

        match(TokenType.STRING_LITERAL);
        if (currentTokenType == TokenType.ARITH_OPERATOR && getCurrentTokenInput().equals("lahamz")) {
            match(TokenType.ARITH_OPERATOR);
            match(TokenType.STRING_LITERAL);
        } else {
            throw new Exception("Unexpected keyword: " + getCurrentToken().getValue());
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

    private String getCurrentTokenInput() {
        return tokens.get(currentTokenIndex).getValue();
    }
}