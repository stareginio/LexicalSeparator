package LexicalSeparator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;

public class SyntaxAnalyzer {

    private List<Token> tokens;
    private int currentTokenIndex;

    private ArrayList<String> parseList = new ArrayList<String>();
    ArrayList<String> tempList = new ArrayList<String>();
    String varName;
    String numVal;
    String strVal;

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
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.STRING_LITERAL);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            match(TokenType.COMMA);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.STRING_LITERAL);
                        } while (getCurrentTokenType() == TokenType.COMMA);
                    }
                    match(TokenType.CLOSEBRACE);
                    System.out.println(tempList);
                    String str = genArrYarn();
                    parseList.add("[<yarn-awit-initialization> [yarn[]][<variableName>[" + varName + "]][=][{][<arrYarnValue>" + str + "[}][;]]");
                } 
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.STRING_LITERAL);
                    if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                        do {
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.ARITH_OPERATOR);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.STRING_LITERAL);
                            System.out.println(getCurrentTokenType());
                        } while (getCurrentTokenInput().equals("lahamz"));
                        String abc = genYarnOp();
                        parseList.add("[<yarn_operation> [yarn][<variableName>[" + varName + "]][=]" + genYarnOp()
                                + "[;]]");
                    } else if (getCurrentTokenInput().equals(";")) {
                        parseList.add("[<yarn-initialization> [yarn][<variableName>[" + varName + "]][=][<string-literal>[" + strVal + "]][;]]");
                    }
                } else if (getCurrentTokenInput().equals(";")){
                    parseList.add("[<yarn_declaration> [yarn][<variableName>[" + varName + "]][;]]");
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
                        String abc = genDigitsOp();
                        parseList.add("[<digit_operation> [digits][<variableName>[" + varName + "]][=]" + genDigitsOp()
                                + "[;]]");
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
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    match(TokenType.POINT);
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.COMMA);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                            match(TokenType.POINT);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.COMMA);

                    }
                    match(TokenType.CLOSEBRACE);
                    String str = genArrLutang();
                    parseList.add("[<lutang_awit_initialization> [digits[]][<variableName>[" + varName
                            + "]][=][{][<arrLutangValue>" + str + "[}][;]]");
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    match(TokenType.POINT);
                    tempList.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                        do {
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.ARITH_OPERATOR);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                            match(TokenType.POINT);
                            tempList.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
                        String abc = genLutangOp();
                        parseList.add("[<lutang_operation> [lutang][<variableName>[" + varName + "]][=]" + genLutangOp()
                                + "[;]]");
                    } else {
                        String n1 = tempList.get(0);
                        String n2 = tempList.get(1);
                        parseList.add("[<lutang_initialization> [lutang][<variableName>[" + varName + "]][=][<numbers>["
                                + n1 + "]][.][<numbers>[" + n2 + "]][;]]");
                    }
                } else {
                    parseList.add("[<lutang_declaration> [lutang][<variableName>[" + varName + "]][;]]");
                }
            }
        }
    }

    private void parseArithOp() throws Exception {
        String prevNumbers = tokens.get(currentTokenIndex-1).getValue();
        ArrayList<String> valuesList = new ArrayList();
        String result = "";
        
        if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
            do {
                match(TokenType.ARITH_OPERATOR);
                String arithOp = tokens.get(currentTokenIndex-1).getValue();
                match(TokenType.NUMBERS);
                String numbers = tokens.get(currentTokenIndex-1).getValue();
                
                // store values
                valuesList.add(arithOp + "," + numbers);
            } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
            
            for (int i = valuesList.size()-1 ; i >= 0 ; i--) {
                String[] split = valuesList.get(i).split(",");
                String arithOp = split[0];
                String numbers = split[1];
                String newResult = "";

                // check if last item
                if (i-1 < 0) {
                    newResult  = " [<arith-operation> [<numbers> [" + prevNumbers + "]]]"
                            + " [<arith-operator> [" + arithOp + "]]"
                            + " [<arith-operation> [<numbers> [" + numbers + "]]]";
                } else {
                    newResult = " [<arith-operation>]"
                            + " [<arith-operator> [" + arithOp + "]]"
                            + " [<arith-operation> [<numbers> [" + numbers + "]]]";
                }
                
                // check if also first item
                if (!result.contains("<arith-operation>")) {
                    result = newResult;
                } else {
                    result = result.replace("[<arith-operation>]", "[<arith-operation> " + newResult + "]");
                }
                
                valuesList.remove(i);
            }
            
            parseList.add("[<arith-operation2> " + result.trim() + "]");
        } else if (getCurrentTokenType() == TokenType.POINT) {
            match(TokenType.POINT);
            match(TokenType.NUMBERS);
            prevNumbers = prevNumbers + "." + tokens.get(currentTokenIndex-1).getValue();
            
            if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                do {
                    match(TokenType.ARITH_OPERATOR);
                    String arithOp = tokens.get(currentTokenIndex-1).getValue();
                    match(TokenType.NUMBERS);
                    String numbers = tokens.get(currentTokenIndex-1).getValue();
                    match(TokenType.POINT);
                    match(TokenType.NUMBERS);
                    numbers += "." + tokens.get(currentTokenIndex-1).getValue();
                    
                    // store values
                    valuesList.add(arithOp + "," + numbers);
                } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);
                
                for (int i = valuesList.size()-1 ; i >= 0 ; i--) {
                    String[] split = valuesList.get(i).split(",");
                    String arithOp = split[0];
                    String numbers = split[1];
                    String newResult = "";

                    // check if last item
                    if (i-1 < 0) {
                        newResult  = " [<arith-operation> [<numbers> [" + prevNumbers + "]]]"
                                + " [<arith-operator> [" + arithOp + "]]"
                                + " [<arith-operation> [<numbers> [" + numbers + "]]]";
                    } else {
                        newResult = " [<arith-operation>]"
                                + " [<arith-operator> [" + arithOp + "]]"
                                + " [<arith-operation> [<numbers> [" + numbers + "]]]";
                    }

                    // check if also first item
                    if (!result.contains("<arith-operation>")) {
                        result = newResult;
                    } else {
                        result = result.replace("[<arith-operation>]", "[<arith-operation> " + newResult + "]");
                    }

                    valuesList.remove(i);
                }

                parseList.add("[<arith-operation2> " + result.trim() + "]");
            }
        } else if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
            match(TokenType.REL_OPERATOR);
            String relOp = tokens.get(currentTokenIndex-1).getValue();
            String type = "";
            String value = "";
            
            // check if variable or value
            if (getCurrentTokenType() == TokenType.IDENTIFIER) { 
                currentTokenIndex++;
                type = "<identifier>";
                value = tokens.get(currentTokenIndex-1).getValue();
            } else if (getCurrentTokenType() == TokenType.NUMBERS) {
                currentTokenIndex++;
                type = "<numbers>";
                value = tokens.get(currentTokenIndex-1).getValue();
            }
            
            result = "[<numbers> [" + prevNumbers + "]]"
                    + " [<rel-operator> [" + relOp + "]]"
                    + " [" + type + " [" + value + "]]" ;
            
            parseList.add("[<rel-expression> " + result.trim() + "]");
        } else {
            throw new Exception("Unexpected keyword: " + getCurrentToken().getValue());
        }
    }

    private void parseLogicalOp() throws Exception {
        Token currentToken = getCurrentToken();

        if (getCurrentTokenType() == TokenType.LOGIC_OPERATOR) {
            do {
                if (currentToken.getValue().equals("naur")) {
                    match(TokenType.LOGIC_OPERATOR);
                    if (getCurrentTokenType() == TokenType.OPENPARENTHESIS) {
                        match(TokenType.OPENPARENTHESIS);
                        parseConditionNumbers();
                        match(TokenType.CLOSEPARENTHESIS);
                    } else if (getCurrentTokenType() == TokenType.IDENTIFIER) {
                        match(TokenType.IDENTIFIER);
                    }
                } else if (currentToken.getValue().equals("naol") || currentToken.getValue().equals("edewups")) {
                    match(TokenType.LOGIC_OPERATOR);
                    if (currentToken.getValue().equals("naur")) { // something wrong
                        match(TokenType.LOGIC_OPERATOR);
                        if (getCurrentTokenType() == TokenType.OPENPARENTHESIS) {
                            match(TokenType.OPENPARENTHESIS);
                            parseConditionNumbers();
                            match(TokenType.CLOSEPARENTHESIS);
                        } else if (getCurrentTokenType() == TokenType.IDENTIFIER) {
                            match(TokenType.IDENTIFIER);
                        }
                    } // walang else
                } else if (getCurrentTokenType() == TokenType.IDENTIFIER) {
                    match(TokenType.IDENTIFIER);
                }
            } while (getCurrentTokenType() == TokenType.LOGIC_OPERATOR);
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

        if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
            match(TokenType.REL_OPERATOR);
            if (getCurrentTokenType() == TokenType.IDENTIFIER
                    || getCurrentTokenType() == TokenType.NUMBERS) { // check if variable or value
                currentTokenIndex++;
            }
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
            if(getCurrentTokenType() == TokenType.LOGIC_OPERATOR){
		parseLogicalOp();
		}
            match(TokenType.CLOSEPARENTHESIS);

            match(TokenType.OPENBRACE);
            parseStatement();
            match(TokenType.CLOSEBRACE);

            System.out.println("getCurrentTokenInput() after tbh: " + getCurrentTokenInput());
            System.out.println("getCurrentTokenType() after tbh: " + getCurrentTokenType());
            System.out.println("currentTokenIndex after tbh: " + (currentTokenIndex + 1));

            // check for "else if" statement/s
            while (getCurrentTokenInput().equals("nvm tbh")) {
                match(TokenType.KEYWORD);

                match(TokenType.OPENPARENTHESIS);
                parseConditionNumbers();
                if(getCurrentTokenType() == TokenType.LOGIC_OPERATOR){
		parseLogicalOp();
		}
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
                if(getCurrentTokenType() == TokenType.LOGIC_OPERATOR){
		parseLogicalOp();
		}
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
            case STRING_LITERAL:
                strVal = getCurrentTokenInput();
        }
        currentTokenIndex++;
    }

    private String genDigitsOp() {
        String str = "";
        int flg = 0;
        for (String x : tempList) {
            if (flg == 0) {
                str = str + "[<numbers>[" + x + "]]";
                flg = 1;
            } else {
                str = str + "[<arith_operator>[" + x + "]]";
                flg = 0;
            }
        }
        return str;
    }
    
    private String genYarnOp() {
        String str = "";
        int flg = 0;
        for (String x : tempList) {
            if (flg == 0) {
                str = str + "[<string-literal>[" + x + "]]";
                flg = 1;
            } else {
                str = str + "[<arith_operator>[" + x + "]]";
                flg = 0;
            }
        }
        return str;
    }
    
    private String genArrYarn() {
        String str = "";
        int len = 0;
        if (tempList.size() == 1) {
            String num1 = tempList.get(0).toString();
            str = "[<string-literal>[" + num1 + "]]]";
        } else {
            for (String x : tempList) {
                if (len == tempList.size() - 1) {
                    continue;
                }
                str = str + "[<arrYarnValue>";
                len++;
            }

            for (String x : tempList) {
                if (x != tempList.get(0)) {
                    str = str + "[,]";
                }
                int i = tempList.size();
                System.out.println(i);
                str = str + "[<string-literal>[" + x + "]]]";
                System.out.println(str);
            }
        }

        return str;
    }
    
    private String genArrDigits() {
        String str = "";
        int len = 0;
        if (tempList.size() == 1) {
            String num1 = tempList.get(0).toString();
            str = "[<numbers>[" + num1 + "]]]";
        } else {
            for (String x : tempList) {
                if (len == tempList.size() - 1) {
                    continue;
                }
                str = str + "[<arrDigitsValue>";
                len++;
            }

            for (String x : tempList) {
                if (x != tempList.get(0)) {
                    str = str + "[,]";
                }
                int i = tempList.size();
                System.out.println(i);
                str = str + "[<numbers>[" + x + "]]]";
                System.out.println(str);
            }
        }

        return str;
    }

    private String genLutangOp() {
        String str = "";
        int flg = 0;
        for (String x : tempList) {
            if (flg == 0) {
                str = str + "[<numbers>[" + x + "]]";
                flg++;
            } else if (flg == 1) {
                str = str + "[.][<numbers>[" + x + "]]";
                flg++;
            } else {
                str = str + "[<arith_operator>[" + x + "]]";
                flg = 0;
            }
        }
        return str;
    }

    private String genArrLutang() {
        String str = "";
        int len = Collections.frequency(tempList, ",") + 1;
        int flg = 0;
        System.out.println(len);

        if (tempList.size() == 2) {
            System.out.println(tempList);
            String num1 = tempList.get(0).toString();
            String num2 = tempList.get(1).toString();
            str = "[<numbers>[" + num1 + "]][.][<numbers>[" + num2 + "]]]";
        } else {
            System.out.println(tempList.size());
            for (int i = 0; i < len; i++) {
                str = str + "[<arrLutangValue>";
            }

            for (String x : tempList) {

                if (x.equals(",")) {
                    str = str + "[,]";
                } else if (flg == 1) {
                    str = str + "[.][<numbers>[" + x + "]]]";
                    flg = 0;
                } else if (flg == 0) {
                    str = str + "[<numbers>[" + x + "]]";
                    flg++;
                }
            }

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