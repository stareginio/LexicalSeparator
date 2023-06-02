package LexicalSeparator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class SyntaxAnalyzer {

    private List<Token> tokens;
    private int currentTokenIndex;
    private SymbolTable symbolTable;

    public SyntaxAnalyzer() {
        symbolTable = new SymbolTable();
    }

    private ArrayList<String> parseList = new ArrayList<String>();
    ArrayList<String> tempListDigitsArr = new ArrayList<String>();
    ArrayList<String> tempListYarnArr = new ArrayList<String>();
    ArrayList<String> tempListLutangArr = new ArrayList<String>();
    ArrayList<String> tempListDigits = new ArrayList<String>();
    ArrayList<String> tempListYarn = new ArrayList<String>();
    ArrayList<String> tempListLutang = new ArrayList<String>();
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
        System.out.println("\nSymbol Table:");
        System.out.println("-------------");

        for (Map.Entry<String, String> entry : symbolTable.getSymbols().entrySet()) {
            String symbolName = entry.getKey();
            String symbolInfo = entry.getValue();

            System.out.println("Value: " + symbolInfo);
            System.out.println("Type: " + symbolName);
            System.out.println("--------------------");
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
            parseArithOp("");
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
            String type = "";
            String value = "";

            if (getCurrentTokenType() == TokenType.OPENBRACKET && tokens.get(currentTokenIndex + 1).getType() == TokenType.CLOSEBRACKET) {
                value = tokens.get(currentTokenIndex + 2).getValue();
            } else {
                value = tokens.get(currentTokenIndex).getValue();
            }
            type = tokens.get(currentTokenIndex - 1).getValue();

            // Add symbol to the symbol table
            if (!symbolTable.containsSymbol(value)) {
                symbolTable.addSymbol(type, value);
            } else {
                throw new Exception("'" + value + "' value already declared.");
            }

            if (getCurrentTokenType() == TokenType.OPENBRACKET) {
                match(TokenType.OPENBRACKET);
                match(TokenType.CLOSEBRACKET);
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.OPENBRACE);
                    tempListYarnArr.add(getCurrentTokenInput());
                    match(TokenType.STRING_LITERAL);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            match(TokenType.COMMA);
                            tempListYarnArr.add(getCurrentTokenInput());
                            match(TokenType.STRING_LITERAL);
                        } while (getCurrentTokenType() == TokenType.COMMA);
                    }
                    match(TokenType.CLOSEBRACE);
                    String str = genArrYarn();

                    String val = str;
                    type = "yarn array (" + value + ") value";

                    // Add symbol to the symbol table
                    symbolTable.addSymbol(type, val);

                    parseList.add("[<yarn-awit-initialization> [yarn[]][<variableName>[" + varName + "]][=][{][<arrYarnValue>" + str + "[}][;]]");
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    tempListYarn.add(getCurrentTokenInput());
                    match(TokenType.STRING_LITERAL);

                    String prevLiteral = tokens.get(currentTokenIndex - 1).getValue();
                    ArrayList<String> valuesList = new ArrayList();
                    String result = "";
                    valuesList.add(prevLiteral);

                    if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                        do {
                            tempListYarn.add(getCurrentTokenInput());
                            match(TokenType.ARITH_OPERATOR);
                            tempListYarn.add(getCurrentTokenInput());
                            match(TokenType.STRING_LITERAL);
                            System.out.println(getCurrentTokenType());

                            valuesList.add(tokens.get(currentTokenIndex - 1).getValue());
                            String type2 = "yarn (" + value + ") operations result";
                            String store = (valuesList.get(0) + valuesList.get(1));
                            String store2 = store.replace("\"", "");
                            valuesList.clear();
                            valuesList.add(store2);

                            if (symbolTable.containsSymbol(type2)) {
                                symbolTable.removeSymbol(type2);
                                symbolTable.addSymbol(type2, store2);
                            } else {
                                symbolTable.addSymbol(type2, store2);
                            }

                        } while (getCurrentTokenInput().equals("lahamz"));
                        String abc = genYarnOp();

                        parseList.add("[<yarn_operation> [yarn][<variableName>[" + varName + "]][=]" + genYarnOp()
                                + "[;]]");
                    } else if (getCurrentTokenInput().equals(";")) {
                        String val = tokens.get(currentTokenIndex - 1).getValue();
                        type = "yarn (" + value + ") value";

                        // Add symbol to the symbol table
                        symbolTable.addSymbol(type, val);
                        parseList.add("[<yarn-initialization> [yarn][<variableName>[" + varName + "]][=][<string-literal>[" + strVal + "]][;]]");
                    }
                } else if (getCurrentTokenInput().equals(";")) {
                    parseList.add("[<yarn_declaration> [yarn][<variableName>[" + varName + "]][;]]");
                }
            }

        } else if (getCurrentTokenInput().equals("digits")) {
            match(TokenType.DATATYPE);
            String type = "";
            String value = "";

            if (getCurrentTokenType() == TokenType.OPENBRACKET && tokens.get(currentTokenIndex + 1).getType() == TokenType.CLOSEBRACKET) {
                value = tokens.get(currentTokenIndex + 2).getValue();
            } else {
                value = tokens.get(currentTokenIndex).getValue();
            }
            type = tokens.get(currentTokenIndex - 1).getValue();

            // Add symbol to the symbol table
            if (!symbolTable.containsSymbol(value)) {
                symbolTable.addSymbol(type, value);
            } else {
                throw new Exception("'" + value + "' value already declared.");
            }
            
            if (getCurrentTokenType() == TokenType.OPENBRACKET) {
                match(TokenType.OPENBRACKET);
                match(TokenType.CLOSEBRACKET);
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.OPENBRACE);
                    tempListDigitsArr.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            match(TokenType.COMMA);
                            tempListDigitsArr.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.COMMA);
                    }
                    match(TokenType.CLOSEBRACE);
                    String str = genArrDigits();

                    String val = str;
                    type = "digits array (" + value + ") value";

                    // Add symbol to the symbol table
                    symbolTable.addSymbol(type, val);

                    parseList.add("[<digits-awit-initialization> [digits[]][<variableName>[" + varName + "]][=][{][<arrDigitsValue>" + str + "[}][;]]");
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    tempListDigits.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                        parseList.add("[<digit_operation> [digits][<variableName>[" + varName + "]][=]");
                        parseArithOp(value);
                        parseList.add("[;]");
                    } else if (getCurrentTokenInput().equals(";")) {
                        String val = tokens.get(currentTokenIndex - 1).getValue();
                        type = "digits (" + value + ") value";

                        // Add symbol to the symbol table
                        symbolTable.addSymbol(type, val);
                        parseList.add("[<digits-initialization> [digits][<variableName>[" + varName + "]][=][<numbers>[" + numVal + "]][;]]");
                    }
                } else if (getCurrentTokenInput().equals(";")) {
                    parseList.add("[<digits-declaration> [digits][<variableName>[" + varName + "]][;]]");
                }
            }
        } else if (getCurrentTokenInput().equals("lutang")) {
            match(TokenType.DATATYPE);
            String type = "";
            String value = "";

            if (getCurrentTokenType() == TokenType.OPENBRACKET && tokens.get(currentTokenIndex + 1).getType() == TokenType.CLOSEBRACKET) {
                value = tokens.get(currentTokenIndex + 2).getValue();
            } else {
                value = tokens.get(currentTokenIndex).getValue();
            }
            type = tokens.get(currentTokenIndex - 1).getValue();

            // Add symbol to the symbol table
            if (!symbolTable.containsSymbol(value)) {
                symbolTable.addSymbol(type, value);
            } else {
                throw new Exception("'" + value + "' value already declared.");
            }

            if (getCurrentTokenType() == TokenType.OPENBRACKET) {
                match(TokenType.OPENBRACKET);
                match(TokenType.CLOSEBRACKET);
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    match(TokenType.OPENBRACE);
                    tempListLutangArr.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    match(TokenType.POINT);
                    tempListLutangArr.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getCurrentTokenType() == TokenType.COMMA) {
                        do {
                            tempListLutangArr.add(getCurrentTokenInput());
                            match(TokenType.COMMA);
                            tempListLutangArr.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                            match(TokenType.POINT);
                            tempListLutangArr.add(getCurrentTokenInput());
                            match(TokenType.NUMBERS);
                        } while (getCurrentTokenType() == TokenType.COMMA);

                    }
                    match(TokenType.CLOSEBRACE);
                    String str = genArrLutang();

                    String val = str;
                    type = "lutang array (" + value + ") value";

                    // Add symbol to the symbol table
                    symbolTable.addSymbol(type, val);

                    parseList.add("[<lutang_awit_initialization> [digits[]][<variableName>[" + varName
                            + "]][=][{][<arrLutangValue>" + str + "[}][;]]");
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    tempListLutang.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    if (getTokenInputLutang() == TokenType.ARITH_OPERATOR) {
                        parseList.add("[<lutang_operation> [lutang][<variableName>[" + varName + "]][=]");
                        parseArithOp(value);
                        parseList.add("[;]");
                    } else if (getCurrentTokenType() == TokenType.POINT) {
                        String numbers = tokens.get(currentTokenIndex - 1).getValue();
                        match(TokenType.POINT);
                        match(TokenType.NUMBERS);
                        numbers += "." + tokens.get(currentTokenIndex - 1).getValue();

                        String val = numbers;
                        type = "lutang (" + value + ") value";

                        // Add symbol to the symbol table
                        symbolTable.addSymbol(type, val);
                        parseList.add("[<lutang_initialization> [lutang][<variableName>[" + varName + "]][=][<numbers>["
                                + numbers + "]][;]]");
                    }
                } else {
                    parseList.add("[<lutang_declaration> [lutang][<variableName>[" + varName + "]][;]]");
                }
            }
        }
    }

    private void parseArithOp(String value) throws Exception {
        int prevNumbers = Integer.parseInt(tokens.get(currentTokenIndex - 1).getValue());
        ArrayList<Integer> valuesList = new ArrayList();
        String result = "";
        valuesList.add(prevNumbers);

        if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
            do {
                match(TokenType.ARITH_OPERATOR);
                String arithOp = tokens.get(currentTokenIndex - 1).getValue();
                match(TokenType.NUMBERS);
                int numbers = Integer.parseInt(tokens.get(currentTokenIndex - 1).getValue());
                
                // compute
                if (arithOp.equals("lahamz")) {

                    valuesList.add(numbers);
                    String store = Integer.toString(valuesList.get(0) + valuesList.get(1));
                    valuesList.clear();
                    valuesList.add(Integer.parseInt(store));

                    String type = "digits (" + value + ") operations result";

                    if (symbolTable.containsSymbol(type)) {
                        symbolTable.removeSymbol(type);
                        symbolTable.addSymbol(type, store);
                    } else {
                        symbolTable.addSymbol(type, store);
                    }

                } else if (arithOp.equals("ghosted")) {

                    valuesList.add(numbers);
                    String store = Integer.toString(valuesList.get(0) - valuesList.get(1));
                    valuesList.clear();
                    valuesList.add(Integer.parseInt(store));

                    String type = "digits (" + value + ") operations result";

                    if (symbolTable.containsSymbol(type)) {
                        symbolTable.removeSymbol(type);
                        symbolTable.addSymbol(type, store);
                    } else {
                        symbolTable.addSymbol(type, store);
                    }

                } else if (arithOp.equals("cheater")) {

                    valuesList.add(numbers);
                    String store = Integer.toString(valuesList.get(0) * valuesList.get(1));
                    valuesList.clear();
                    valuesList.add(Integer.parseInt(store));

                    String type = "digits (" + value + ") operations result";

                    if (symbolTable.containsSymbol(type)) {
                        symbolTable.removeSymbol(type);
                        symbolTable.addSymbol(type, store);
                    } else {
                        symbolTable.addSymbol(type, store);
                    }

                } else if (arithOp.equals("chariz")) {

                    valuesList.add(numbers);
                    String store = Integer.toString(valuesList.get(0) / valuesList.get(1));
                    valuesList.clear();
                    valuesList.add(Integer.parseInt(store));

                    String type = "digits (" + value + ") operations result";

                    if (symbolTable.containsSymbol(type)) {
                        symbolTable.removeSymbol(type);
                        symbolTable.addSymbol(type, store);
                    } else {
                        symbolTable.addSymbol(type, store);
                    }
                    
                } else if (arithOp.equals("dasurv")) {

                    valuesList.add(numbers);
                    String store = Integer.toString(valuesList.get(0) % valuesList.get(1));
                    valuesList.clear();
                    valuesList.add(Integer.parseInt(store));

                    String type = "digits (" + value + ") operations result";

                    if (symbolTable.containsSymbol(type)) {
                        symbolTable.removeSymbol(type);
                        symbolTable.addSymbol(type, store);
                    } else {
                        symbolTable.addSymbol(type, store);
                    }
                }
                
            } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);

            for (int i = valuesList.size() - 1; i >= 0; i--) {
//                String[] split = valuesList.get(i).split(",");
//                String arithOp = split[0];
//                String numbers = split[1];
                String newResult = "";

                // check if last item
                if (i - 1 < 0) {
//                    newResult = " [<arith-operation> [<numbers> [" + prevNumbers + "]]]"
                    //                           + " [<arith-operator> [" + arithOp + "]]"
//                            + " [<arith-operation> [<numbers> [" + numbers + "]]]";
                } else {
//                    newResult = " [<arith-operation>]"
//                            + " [<arith-operator> [" + arithOp + "]]"
//                            + " [<arith-operation> [<numbers> [" + numbers + "]]]";
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
//            prevNumbers = prevNumbers + "." + tokens.get(currentTokenIndex - 1).getValue();

            if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                do {
                    match(TokenType.ARITH_OPERATOR);
                    String arithOp = tokens.get(currentTokenIndex - 1).getValue();
                    match(TokenType.NUMBERS);
                    String numbers = tokens.get(currentTokenIndex - 1).getValue();
                    match(TokenType.POINT);
                    match(TokenType.NUMBERS);
                    numbers += "." + tokens.get(currentTokenIndex - 1).getValue();

                    // store values
                    //                   valuesList.add(arithOp + "," + numbers);
                } while (getCurrentTokenType() == TokenType.ARITH_OPERATOR);

                for (int i = valuesList.size() - 1; i >= 0; i--) {
//                    String[] split = valuesList.get(i).split(",");
//                    String arithOp = split[0];
//                    String numbers = split[1];
                    String newResult = "";

                    // check if last item
                    if (i - 1 < 0) {
                        //                       newResult = " [<arith-operation> [<numbers> [" + prevNumbers + "]]]"
//                                + " [<arith-operator> [" + arithOp + "]]"
//                                + " [<arith-operation> [<numbers> [" + numbers + "]]]";
                    } else {
                        //                       newResult = " [<arith-operation>]"
//                                + " [<arith-operator> [" + arithOp + "]]"
//                                + " [<arith-operation> [<numbers> [" + numbers + "]]]";
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
            String relOp = tokens.get(currentTokenIndex - 1).getValue();
            String type = "";
//            String value = "";

            // check if variable or value
            if (getCurrentTokenType() == TokenType.IDENTIFIER) {
                currentTokenIndex++;
                type = "<identifier>";
                value = tokens.get(currentTokenIndex - 1).getValue();

                // Add symbol to the symbol table
//                symbolTable.addSymbol(value, new SymbolInfo(value, TokenType.IDENTIFIER));
            } else if (getCurrentTokenType() == TokenType.NUMBERS) {
                currentTokenIndex++;
                type = "<numbers>";
                value = tokens.get(currentTokenIndex - 1).getValue();

                // Add symbol to the symbol table
//                symbolTable.addSymbol(value, new SymbolInfo(value, TokenType.NUMBERS));
            }

            result = "[<numbers> [" + prevNumbers + "]]"
                    + " [<rel-operator> [" + relOp + "]]"
                    + " [" + type + " [" + value + "]]";

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
                        checkRelorLog();
                        match(TokenType.CLOSEPARENTHESIS);
                    } else if (getCurrentTokenType() == TokenType.IDENTIFIER) {
                        match(TokenType.IDENTIFIER);
                        if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
                            checkRelorLog();
                        }
                    } else if (getCurrentTokenType() == TokenType.NUMBERS) {
                        match(TokenType.NUMBERS);
                        if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
                            checkRelorLog();
                        }
                    }
                } else if (currentToken.getValue().equals("naol") || currentToken.getValue().equals("edewups")) {
                    match(TokenType.LOGIC_OPERATOR);
                    if (getCurrentTokenType() == TokenType.IDENTIFIER) {
                        match(TokenType.IDENTIFIER);
                        checkRelorLog();
                    } else if (getCurrentTokenType() == TokenType.NUMBERS) {
                        match(TokenType.NUMBERS);
                        if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
                            checkRelorLog();
                        }
                    }
                }
            } while (getCurrentTokenType() == TokenType.LOGIC_OPERATOR);
        }
    }

    private void checkRelorLog() throws Exception {

        if (getCurrentTokenType() == TokenType.NUMBERS) {
            match(TokenType.NUMBERS);
            parseConditionNumbers();
            if (getCurrentTokenType() == TokenType.LOGIC_OPERATOR) {
                parseLogicalOp();
            }
        } else if (getCurrentTokenType() == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
            if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
                parseConditionNumbers();
                if (getCurrentTokenType() == TokenType.LOGIC_OPERATOR) {
                    Token currentToken = getCurrentToken();
                    if (currentToken.getValue().equals("naol") || currentToken.getValue().equals("edewups")) {
                        parseLogicalOp();
                    }
                }
            } else if (getCurrentTokenType() == TokenType.LOGIC_OPERATOR) {
                parseLogicalOp();
            }

        } else if (getCurrentTokenType() == TokenType.LOGIC_OPERATOR) {
            parseLogicalOp();
        } else if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
            parseConditionNumbers();
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
//            ArrayList<String> conditionList = new ArrayList<String>();
//            ArrayList<String> statementListIf = new ArrayList<String>();
//            ArrayList<String> statementListElseIf = new ArrayList<String>();
//            ArrayList<String> statementListElse = new ArrayList<String>();
//            ArrayList<Integer> elseIfStatementCtr = new ArrayList<Integer>();
            
            match(TokenType.KEYWORD);
            match(TokenType.OPENPARENTHESIS);
            checkRelorLog();
            match(TokenType.CLOSEPARENTHESIS);
            
            // transfer the parsed rel or log statement from parseList to conditionList for later
//            conditionList.add(parseList.get(parseList.size()-1));
//            parseList.remove(parseList.size()-1);
            
            match(TokenType.OPENBRACE);
            while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                parseStatement();
                
                // transfer the parsed statement from parseList to statementListIf for later
//                statementListIf.add(parseList.get(parseList.size()-1));
//                parseList.remove(parseList.size()-1);
            }
            match(TokenType.CLOSEBRACE);

            // check for "else if" statement/s
            if (currentTokenIndex < tokens.size()) {
                while (getCurrentTokenInput().equals("nvm tbh")) {
                    match(TokenType.KEYWORD);
                    
                    match(TokenType.OPENPARENTHESIS);
                    checkRelorLog();
                    match(TokenType.CLOSEPARENTHESIS);
                    
                    // transfer the parsed rel or log statement from parseList to conditionList for later
//                    conditionList.add(parseList.get(parseList.size()-1));
//                    parseList.remove(parseList.size()-1);
                    
//                    int ctr = 0;
                    match(TokenType.OPENBRACE);
                    while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                        parseStatement();
                        
                        // transfer the parsed statement from parseList to statementListElseIf for later
//                        statementListElseIf.add(parseList.get(parseList.size()-1));
//                        parseList.remove(parseList.size()-1);
                        
//                        ctr++;
                    }
                    match(TokenType.CLOSEBRACE);
                    
//                    elseIfStatementCtr.add(ctr);
                }

                // check for "else" statement
                if (getCurrentTokenInput().equals("nvm")) { // if-else statement
                    match(TokenType.KEYWORD);
                    
                    match(TokenType.OPENBRACE);
                    while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                        parseStatement();   // statement/s
                        
                        // transfer the parsed statement from parseList to statementListElse for later
//                        statementListElse.add(parseList.get(parseList.size()-1));
//                        parseList.remove(parseList.size()-1);
                    }
                    match(TokenType.CLOSEBRACE);
                    
//                    // if
//                    String parseStr = "[<if-else-statement> [tbh] [(] [<condition> " + conditionList.get(0) + "] [)] [{] ";
//                    for (int i=0; i < statementListIf.size(); i++) {
//                        parseStr += "[<statement> " + statementListIf.get(i) + "]";
//                    }
//                    parseStr += " [}]";
//                    
//                    // else-if's
//                    int conditionCtr = 1;
//                    while (!elseIfStatementCtr.isEmpty()) {
//                        parseStr += "[nvm-tbh] [(] [<condition> " + conditionList.get(conditionCtr) + "]";
//                        for (int i=0; i < statementListElseIf.size(); i++) {
//                            parseStr += " [<statement> " + statementListElseIf.get(i) + "] ";
//                        }
//                        parseStr += "[}] ";
//                        
//                        conditionCtr++;
//                        elseIfStatementCtr.remove(0);
//                    }
//                    
//                    // else
//                    parseStr += "[nvm] [{] ";
//                    for (int i=0; i < statementListElse.size(); i++) {
//                        parseStr += "[<statement> " + statementListElse.get(i) + "]";
//                    }
//                    
//                    parseStr += " ]";
//                    parseList.add(parseStr);
                } else {    // if-else chain
                    // if
//                    String parseStr = "[<if-else-statement> [tbh] [(] [<condition> " + conditionList.get(0) + "] [)] [{] ";
//                    for (int i=0; i < statementListIf.size(); i++) {
//                        parseStr += "[<statement> " + statementListIf.get(i) + "]";
//                    }
//                    parseStr += " [}]";
//                    
//                    // else-if's
//                    int conditionCtr = 1;
//                    while (!elseIfStatementCtr.isEmpty()) {
//                        parseStr += "[nvm-tbh] [(] [<condition> " + conditionList.get(conditionCtr) + "]";
//                        for (int i=0; i < statementListElseIf.size(); i++) {
//                            parseStr += " [<statement> " + statementListElseIf.get(i) + "] ";
//                        }
//                        parseStr += "[}] ";
//                        
//                        conditionCtr++;
//                        elseIfStatementCtr.remove(0);
//                    }
//                    
//                    parseStr += " ]";
//                    parseList.add(parseStr);
                }
            } else {    // if statement
//                String parseStr = "[<if-statement> [tbh] [(] [<condition> " + conditionList.get(0) + "] [)] [{] ";
//                for (int i=0; i < statementListIf.size(); i++) {
//                    parseStr += "[<statement> " + statementListIf.get(i) + "]";
//                }
//                
//                parseStr += " [}]]";
//                parseList.add(parseStr);
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
                while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                    parseStatement();
                }
            }

            match(TokenType.CLOSEBRACE);

            // throw error if vibe check keyword is not detected
            if (getCurrentTokenType() != TokenType.KEYWORD
                    && !getCurrentTokenInput().equals("vibe check")) {
                throw new Exception("Expected keyword vibe check, but found " + getCurrentTokenType());
            } else {
                match(TokenType.KEYWORD);

                match(TokenType.OPENPARENTHESIS);
                checkRelorLog();
                match(TokenType.CLOSEPARENTHESIS);

                match(TokenType.SEMICOLON);
            }
        } else if (getCurrentTokenInput().equals("vibe check")) {  // while loop
            match(TokenType.KEYWORD);

            if (getCurrentTokenType() == TokenType.IDENTIFIER
                    || getCurrentTokenType() == TokenType.NUMBERS) { // check if variable or value
                currentTokenIndex++;
            }

            match(TokenType.OPENPARENTHESIS);
            checkRelorLog();
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
                    while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                        parseStatement();
                    }
                }
            } else {
                while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                    parseStatement();
                }
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
            checkRelorLog();    // condition involving numbers

            match(TokenType.SEMICOLON);
            parseIteration();   // iteration

            match(TokenType.CLOSEPARENTHESIS);
            match(TokenType.OPENBRACE);
            
            while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                parseStatement();   // statement/s
            }

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
        match(TokenType.REL_OPERATOR);
        if (getCurrentTokenType() == TokenType.IDENTIFIER || getCurrentTokenType() == TokenType.NUMBERS) {
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
        for (String x : tempListDigits) {
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
        for (String x : tempListYarn) {
            if (flg == 0) {
                str = str + x;
                flg = 1;
            } else {
                str = str + x;
                flg = 0;
            }
        }
        return str;
    }

    private String genArrYarn() {
        String str = "{";
        int len = 0;
        if (tempListYarnArr.size() == 1) {
            String num1 = tempListYarnArr.get(0).toString();
            str = num1;
        } else {
            for (String x : tempListYarnArr) {
                if (len == tempListYarnArr.size() - 1) {
                    continue;
                }
                str = str;
                len++;
            }

            for (String x : tempListYarnArr) {
                if (x != tempListYarnArr.get(0)) {
                    str = str + ",";
                }
                int i = tempListYarnArr.size();
                str = str + x;
            }
        }
        str = str + "}";
        return str;
    }

    private String genArrDigits() {
        String str = "{";
        int len = 0;
        if (tempListDigitsArr.size() == 1) {
            String num1 = tempListDigitsArr.get(0).toString();
            str = num1;
        } else {
            for (String x : tempListDigitsArr) {
                if (len == tempListDigitsArr.size() - 1) {
                    continue;
                }
                str = str;
                len++;
            }

            for (String x : tempListDigitsArr) {
                if (x != tempListDigitsArr.get(0)) {
                    str = str + ",";
                }
                int i = tempListDigitsArr.size();
                str = str + x;
            }

        }
        str = str + "}";
        return str;
    }

    private String genLutangOp() {
        String str = "";
        int flg = 0;
        for (String x : tempListLutang) {
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
        String str = "{";
        int len = Collections.frequency(tempListLutangArr, ",") + 1;
        int flg = 0;
        System.out.println(len);

        if (tempListLutangArr.size() == 2) {
            System.out.println(tempListLutangArr);
            String num1 = tempListLutangArr.get(0).toString();
            String num2 = tempListLutangArr.get(1).toString();
            str = num1 + "." + num2;
        } else {
            for (int i = 0; i < len; i++) {
                str = str;
            }

            for (String x : tempListLutangArr) {

                if (x.equals(",")) {
                    str = str + ",";
                } else if (flg == 1) {
                    str = str + "." + x;
                    flg = 0;
                } else if (flg == 0) {
                    str = str + x;
                    flg++;
                }
            }
        }
        str = str + "}";
        return str;
    }

    private TokenType getTokenInputLutang() {
        return tokens.get(currentTokenIndex + 2).getType();
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
