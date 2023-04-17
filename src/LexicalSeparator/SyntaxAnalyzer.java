package LexicalSeparator;

import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;

public class SyntaxAnalyzer {

    private List<Token> tokens;
    private int currentTokenIndex;

    private ArrayList<String> parseList = new ArrayList<String>();
    ArrayList<String> tempList = new ArrayList<String>();
    String varName;
    String numVal;

    public void analyze(List<Token> tokens) throws Exception {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        
        parseProgram();
        
        FileWriter parseWriter = new FileWriter("parseTree.txt");
        for (String str : parseList) {
            parseWriter.write(str + System.lineSeparator());
        }
        parseWriter.close();
    }

    private void parseProgram() throws Exception {
        while (currentTokenIndex < tokens.size()) {
            parseStatement();
        }
    }

    private void parseStatement() throws Exception {
        TokenType currentTokenType = getCurrentTokenType();
        
        if (currentTokenType == TokenType.DATATYPE) {
            parseVariableDeclaration();
            match(TokenType.SEMICOLON);
        } else if (currentTokenType == TokenType.IDENTIFIER) {
            parseAssignment();
            match(TokenType.SEMICOLON);
        } else if (currentTokenType == TokenType.KEYWORD) {
            parseKeyword();
        } else if (currentTokenType == TokenType.NUMBERS) {
            match(TokenType.NUMBERS);
            parseArithOp();
            match(TokenType.SEMICOLON);
        } else if (currentTokenType == TokenType.STRING_LITERAL) {
            parseAssignOp();
            match(TokenType.SEMICOLON);
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
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            match(TokenType.COMMA);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.COMMA);
                    }
                    match(TokenType.CLOSEBRACE);
                    System.out.println(tempList);
                    String str = genArrDigits();
                    parseList.add("[<digits-awit-initialization> [digits[]][<variableName>[" + varName + "]][=][{][<arrDigitsValue>" + str + "[}][;]]");
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                        do {
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.ARITH_OPERATOR);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
                        String abc = genMultiOp();
                        parseList.add("[<digit_operation> [digits][<variableName>[" + varName + "]][=]" + genMultiOp()+ "[;]]");
                    } else if (getCurrentTokenInput().equals(";")) {
                        parseList.add("[<digits-initialization> [digits][<variableName>[" + varName + "]][=][<numbers>[" + numVal + "]][;]]");
                    }
                } else if (getCurrentTokenInput().equals(";")) {
                    parseList.add("[<digits-declaration> [digits][<variableName>[" + varName + "]][;]]");
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
        if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
            do {
                match(TokenType.ARITH_OPERATOR);
                match(TokenType.NUMBERS);
            } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
        } else if (getCurrentTokenType() == TokenType.POINT) {
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
        } else {
            throw new Exception("Unexpected keyword: " + getCurrentToken().getValue());
        }
    }

    private void parseAssignOp() throws Exception {
        match(TokenType.STRING_LITERAL);
        if (getCurrentTokenInput().equals("lahamz") || getCurrentTokenInput().equals("ghosted")
                || getCurrentTokenInput().equals("cheater") || getCurrentTokenInput().equals("chariz")
                || getCurrentTokenInput().equals("dasurv")) {
            match(TokenType.ARITH_OPERATOR);
            match(TokenType.STRING_LITERAL);
        } else {
            throw new Exception("Unexpected keyword: " + getCurrentToken().getValue());
        }
    }

    private void parseAssignment() throws Exception {
        parseVariableName();
        if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
            match(TokenType.ASSIGNMENT_OPERATOR);
            parseExpression();
        }
        
        if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
            do {
                match(TokenType.ARITH_OPERATOR);
                if (getCurrentTokenType() == TokenType.IDENTIFIER) {    // check if variable
                    parseVariableName();
                } else if (getCurrentTokenType() == TokenType.NUMBERS) {  // check if value
                    match(TokenType.NUMBERS);
                }
            } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
        }
    }

    private void parseVariableName() throws Exception {
        match(TokenType.IDENTIFIER);
    }

    private void parseKeyword() throws Exception {       
        if (getCurrentTokenInput().equals("tbh")) {    //if / if-else chain / if-else statement
            match(TokenType.KEYWORD);
            
            match(TokenType.OPENPARENTHESIS);
            parseConditionNumbers();
            match(TokenType.CLOSEPARENTHESIS);
            
            match(TokenType.OPENBRACE);
            parseStatement();
            match(TokenType.CLOSEBRACE);
            
            System.out.println("getCurrentTokenInput() after tbh: " + getCurrentTokenInput());
            System.out.println("getCurrentTokenType() after tbh: " + getCurrentTokenType());
            System.out.println("ccurrentTokenIndex after tbh: " + (currentTokenIndex + 1));
            
            // check for "else if" statement/s
            while (getCurrentTokenInput().equals("nvm tbh")) {
                match(TokenType.KEYWORD);
                    
                match(TokenType.OPENPARENTHESIS);
                parseConditionNumbers();
                match(TokenType.CLOSEPARENTHESIS);

                match(TokenType.OPENBRACE);
                parseStatement();
                match(TokenType.CLOSEBRACE);
            }
            
            // check for "else" statement
            if (getCurrentTokenInput().equals("nvm")) {
                match(TokenType.KEYWORD);
                match(TokenType.OPENBRACE);
                parseStatement();
                match(TokenType.CLOSEBRACE);
            }
        } else if (getCurrentTokenInput().equals("nvm tbh") || getCurrentTokenInput().equals("nvm")) {
            throw new Exception("Keyword " + getCurrentTokenType() + " found without a previous 'tbh' statement");
        } else if (getCurrentTokenInput().equals("g")) {   // do-while loop
            match(TokenType.KEYWORD);
            match(TokenType.OPENBRACE);
            
            // check if iteration
            int nextTokenIndex = currentTokenIndex + 1;
            if ((getCurrentTokenType() == TokenType.UNARY_OPERATOR
                    && (nextTokenIndex < tokens.size()) && tokens.get(nextTokenIndex).getType() == TokenType.IDENTIFIER)
                    || (getCurrentTokenType() == TokenType.IDENTIFIER
                    && (nextTokenIndex < tokens.size()) && tokens.get(nextTokenIndex).getType() == TokenType.UNARY_OPERATOR)) {
                parseIteration();
            } else {
                parseStatement();
            }
            
            match(TokenType.CLOSEBRACE);
            
            // throw error if vibe check keyword is not detected
            if (getCurrentTokenType() != TokenType.KEYWORD
                    && !getCurrentTokenInput().equals("vibe check")) {
                 throw new Exception("Expected keyword vibe check, but found " + getCurrentTokenType());
            } else {
                match(TokenType.KEYWORD);
            
                match(TokenType.OPENPARENTHESIS);
                parseConditionNumbers();
                match(TokenType.CLOSEPARENTHESIS);
                
                match(TokenType.SEMICOLON);
            }
        } else if (getCurrentTokenInput().equals("vibe check")) {  // while loop
            match(TokenType.KEYWORD);
            
            match(TokenType.OPENPARENTHESIS);
            parseConditionNumbers();
            match(TokenType.CLOSEPARENTHESIS);

            match(TokenType.OPENBRACE);

            // check if digits initialization
            if (getCurrentTokenInput().equals("digits")) {
                int nextTokenIndex = currentTokenIndex + 1;
                if ((getCurrentTokenType() == TokenType.UNARY_OPERATOR
                        && (nextTokenIndex < tokens.size()) && tokens.get(nextTokenIndex).getType() == TokenType.IDENTIFIER)
                        || (getCurrentTokenType() == TokenType.IDENTIFIER
                        && (nextTokenIndex < tokens.size()) && tokens.get(nextTokenIndex).getType() == TokenType.UNARY_OPERATOR)) {
                    parseIteration();
                } else {
                    parseStatement();
                }
            } else {
                parseStatement();
            }

            match(TokenType.CLOSEBRACE);
        } else if (getCurrentTokenInput().equals("forda")) {   // for loop           
            match(TokenType.KEYWORD);
            match(TokenType.OPENPARENTHESIS);
            
            // check if digits initialization
            if (getCurrentTokenInput().equals("digits")) {
                match(TokenType.DATATYPE);
                match(TokenType.IDENTIFIER);
                match(TokenType.ASSIGNMENT_OPERATOR);
                if (getCurrentTokenType() == TokenType.IDENTIFIER
                        || getCurrentTokenType() == TokenType.NUMBERS) { // check if variable or value
                    currentTokenIndex++;
                }
            } else {
                throw new Exception("Expected digits expression, but found " + getCurrentTokenInput());
            }
            
            match(TokenType.SEMICOLON);
            parseConditionNumbers();    // condition involving numbers
            
            match(TokenType.SEMICOLON);
            parseIteration();   // iteration
            
            match(TokenType.CLOSEPARENTHESIS);
            match(TokenType.OPENBRACE);
            
            parseStatement();  // statement
            
            match(TokenType.CLOSEBRACE);
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

    private void parseIteration() throws Exception {
        // check if starting with unary operator
        if (getCurrentTokenType() == TokenType.UNARY_OPERATOR) {
            match(TokenType.UNARY_OPERATOR);
            match(TokenType.IDENTIFIER);
        } else {
            match(TokenType.IDENTIFIER);
            match(TokenType.UNARY_OPERATOR);
        }
    }
    
    private void parseConditionNumbers() throws Exception {
        match(TokenType.IDENTIFIER);
        match(TokenType.REL_OPERATOR);
        if (getCurrentTokenType() == TokenType.IDENTIFIER
                || getCurrentTokenType() == TokenType.NUMBERS) { // check if variable or value
            currentTokenIndex++;
        }
    }
    
    private void match(TokenType expectedTokenType) throws Exception {
        if (getCurrentTokenType() != expectedTokenType) {
            throw new Exception("Expected token type " + expectedTokenType + ", but found " + getCurrentTokenType()
            + " with value " + getCurrentTokenInput() + " at token " + (currentTokenIndex + 1));
        }
        switch (getCurrentTokenType()) {
            case IDENTIFIER:
                varName = getCurrentTokenInput();
            case NUMBERS:
                numVal = getCurrentTokenInput();    
        }
        currentTokenIndex++;
    }

    private String genMultiOp() {
        String str = "";
        int flag = 0;
        for (String x : tempList) {
            if (flag == 0) {
                str = str + "[<numbers>[" + x + "]]";
                flag = 1;
            } else {
                str = str + "[<arith_operator>[" + x + "]]";
                flag = 0;
            }
        }
        return str;
    }

    private String genArrDigits() {
        String str = "";
        int len = 0;
        if (tempList.size() == 1) {
            String num1 = tempList.get(0).toString();
            str = "[<numbers>[" + num1 +"]]";
        } else {
           for (String x : tempList) {
            if (len == tempList.size() - 1) {
                continue;
            }
                str = str + "[<arrDigitsValue>";
                len++;
            }
            }
            for (String x : tempList) {
                int i = tempList.size();
                System.out.println(i);
                    str = str + "[<numbers>[" + x + "]]]";
                System.out.println(str);
                }
            return str;
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