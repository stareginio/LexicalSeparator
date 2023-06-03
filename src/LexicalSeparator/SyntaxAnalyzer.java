package LexicalSeparator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

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
    boolean[] condition = {false, false};  // if inside a control structure, if the condition is true

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
            
            // Check if inside a control structure with condition = true
            // or if not inside a control structure
            if ((condition[0] && condition[1]) || !condition[0]) {
                // Add symbol to the symbol table
                if (!symbolTable.containsSymbol(value)) {
                    symbolTable.addSymbol(type, value);
                } else {
                    throw new Exception("'" + value + "' value already declared.");
                }
            }
            
            // Check if array
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
                    
                    // Check if inside a control structure with condition = true
                    // or if not inside a control structure
                    if ((condition[0] && condition[1]) || !condition[0]) {
                        // Add symbol to the symbol table
                        symbolTable.addSymbol(type, val);
                    }

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
                            
                            // Check if inside a control structure with condition = true
                            // or if not inside a control structure
                            if ((condition[0] && condition[1]) || !condition[0]) {
                                if (symbolTable.containsSymbol(type2)) {
                                    symbolTable.removeSymbol(type2);
                                    symbolTable.addSymbol(type2, store2);
                                } else {
                                    symbolTable.addSymbol(type2, store2);
                                }
                            }

                        } while (getCurrentTokenInput().equals("lahamz"));
                        String abc = genYarnOp();

                        parseList.add("[<yarn_operation> [yarn][<variableName>[" + varName + "]][=]" + genYarnOp()
                                + "[;]]");
                    } else if (getCurrentTokenInput().equals(";")) {
                        String val = tokens.get(currentTokenIndex - 1).getValue();
                        type = "yarn (" + value + ") value";
                        
                        // Check if inside a control structure with condition = true
                        // or if not inside a control structure
                        if ((condition[0] && condition[1]) || !condition[0]) {
                            // Add symbol to the symbol table
                            symbolTable.addSymbol(type, val);
                        }
                        
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
            
            // Check if inside a control structure with condition = true
            // or if not inside a control structure
            if ((condition[0] && condition[1]) || !condition[0]) {
                // Add symbol to the symbol table
                if (!symbolTable.containsSymbol(value)) {
                    symbolTable.addSymbol(type, value);
                } else {
                    throw new Exception("'" + value + "' value already declared.");
                }
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
                    
                    // Check if inside a control structure with condition = true
                    // or if not inside a control structure
                    if ((condition[0] && condition[1]) || !condition[0]) {
                        // Add symbol to the symbol table
                        symbolTable.addSymbol(type, val);
                    }

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
                        
                        // Check if inside a control structure with condition = true
                        // or if not inside a control structure
                        if ((condition[0] && condition[1]) || !condition[0]) {
                            // Add symbol to the symbol table
                            symbolTable.addSymbol(type, val);
                        }
                        
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
            
            // Check if inside a control structure with condition = true
            // or if not inside a control structure
            if ((condition[0] && condition[1]) || !condition[0]) {
                // Add symbol to the symbol table
                if (!symbolTable.containsSymbol(value)) {
                    symbolTable.addSymbol(type, value);
                } else {
                    throw new Exception("'" + value + "' value already declared.");
                }
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
                    
                    // Check if inside a control structure with condition = true
                    // or if not inside a control structure
                    if ((condition[0] && condition[1]) || !condition[0]) {
                        // Add symbol to the symbol table
                        symbolTable.addSymbol(type, val);
                    }                    

                    parseList.add("[<lutang_awit_initialization> [digits[]][<variableName>[" + varName
                            + "]][=][{][<arrLutangValue>" + str + "[}][;]]");
                }
            } else {
                parseVariableName();
                if (getCurrentTokenType() == TokenType.ASSIGNMENT_OPERATOR) {
                    match(TokenType.ASSIGNMENT_OPERATOR);
                    tempListLutang.add(getCurrentTokenInput());
                    match(TokenType.NUMBERS);
                    
                    // For debugging
//                    System.out.println("tokens.get(currentTokenIndex): " + tokens.get(currentTokenIndex));
//                    System.out.println("getTokenInputLutang(): " + getTokenInputLutang());
                    
                    if (getTokenInputLutang() == TokenType.ARITH_OPERATOR
                            || tokens.get(currentTokenIndex).getType() == TokenType.ARITH_OPERATOR) {                       
                        parseList.add("[<lutang_operation> [lutang][<variableName>[" + varName + "]][=]");
                        parseArithOp(value);
                        parseList.add("[;]");
                    } if (getCurrentTokenType() == TokenType.POINT) {                       
                        // Check for arith operation
                        if (currentTokenIndex+2 < tokens.size()
                                && tokens.get(currentTokenIndex+2).getType() == TokenType.ARITH_OPERATOR) {
                            parseArithOp(value);
                        } else {
                            String numbers = tokens.get(currentTokenIndex - 1).getValue();
                            match(TokenType.POINT);
                            match(TokenType.NUMBERS);
                            numbers += "." + tokens.get(currentTokenIndex - 1).getValue();
                            
                            String val = numbers;
                            type = "lutang (" + value + ") value";
                            
                            // Check if inside a control structure with condition = true
                            // or if not inside a control structure
                            if ((condition[0] && condition[1]) || !condition[0]) {
                                // Add symbol to the symbol table
                                symbolTable.addSymbol(type, val);
                            }
                            
                            parseList.add("[<lutang_initialization> [lutang][<variableName>[" + varName + "]][=][<numbers>["
                                    + numbers + "]][;]]");
                        }   
                    }
                } else {
                    parseList.add("[<lutang_declaration> [lutang][<variableName>[" + varName + "]][;]]");
                }
            }
        }
    }

    private void parseArithOp(String value) throws Exception {
        String prevNumbers = tokens.get(currentTokenIndex - 1).getValue();
        TokenType prevTokenType = tokens.get(currentTokenIndex - 1).getType();
        
        ArrayList<Object> valuesList = new ArrayList();
        String result = "";
        
        Map.Entry<String,String> firstItem = symbolTable.getSymbols().entrySet().iterator().next();
        String datatype = firstItem.getKey();
        
        // Check if not followed by a decimal point (int or identifier)
        if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
            // Check if int or identifier
            if (prevTokenType == TokenType.NUMBERS) {
                // add the integer in valuesList
                valuesList.add(Integer.parseInt(prevNumbers));
            } else if (prevTokenType == TokenType.IDENTIFIER) {
                value = tokens.get(currentTokenIndex-1).getValue();
                String numbers = "";
                
                // Retrieve the value of the identifier from the symbolTable
                for (Map.Entry<String, String> entry : symbolTable.getSymbols().entrySet()) {
                    if (entry.getKey().contains("(" + value + ")")) {
                        numbers = entry.getValue();
                        datatype = entry.getKey().substring(0, entry.getKey().indexOf(" "));
                        break;
                    }
                }
                
                // Throw error if id is not in the symbolTable (if idValue is null)
                if (numbers.equals("")) {
                    throw new Exception("'" + value + "' variable not declared.");
                }
                
                // Check if int or float
                if (datatype.equals("digits")) {
                    // add the integer value in valuesList
                    valuesList.add(Integer.parseInt(numbers));
                } else if (datatype.equals("lutang")) {
                    // add the float value in valuesList
                    valuesList.add(Float.parseFloat(numbers));
                }
            }
            
            do {
                if (datatype.equals("digits")) {
                    match(TokenType.ARITH_OPERATOR);
                    String arithOp = tokens.get(currentTokenIndex - 1).getValue();
                    match(TokenType.NUMBERS);
                    int numbers = Integer.parseInt(tokens.get(currentTokenIndex - 1).getValue());
                    String type = "", store = "";
                    
                    // compute
                    if (arithOp.equals("lahamz")) {
                        valuesList.add(numbers);
                        store = Integer.toString((Integer) valuesList.get(0) + (Integer) valuesList.get(1));
                        valuesList.clear();
                        valuesList.add(Integer.parseInt(store));

                        type = "digits (" + value + ") operations result";
                        
                    } else if (arithOp.equals("ghosted")) {
                        valuesList.add(numbers);
                        store = Integer.toString((Integer) valuesList.get(0) + (Integer) valuesList.get(1));
                        valuesList.clear();
                        valuesList.add(Integer.parseInt(store));

                        type = "digits (" + value + ") operations result";
                        
                    } else if (arithOp.equals("cheater")) {
                        valuesList.add(numbers);
                        store = Integer.toString((Integer) valuesList.get(0) + (Integer) valuesList.get(1));
                        valuesList.clear();
                        valuesList.add(Integer.parseInt(store));

                        type = "digits (" + value + ") operations result";

                    } else if (arithOp.equals("chariz")) {
                        valuesList.add(numbers);
                        store = Integer.toString((Integer) valuesList.get(0) + (Integer) valuesList.get(1));
                        valuesList.clear();
                        valuesList.add(Integer.parseInt(store));

                        type = "digits (" + value + ") operations result";

                    } else if (arithOp.equals("dasurv")) {
                        valuesList.add(numbers);
                        store = Integer.toString((Integer) valuesList.get(0) + (Integer) valuesList.get(1));
                        valuesList.clear();
                        valuesList.add(Integer.parseInt(store));

                        type = "digits (" + value + ") operations result";
                    }
                    
                    // Check if inside a control structure with condition = true
                    // or if not inside a control structure
                    if ((condition[0] && condition[1]) || !condition[0]) {
                        if (symbolTable.containsSymbol(type)) {
                            symbolTable.removeSymbol(type);
                            symbolTable.addSymbol(type, store);
                        } else {
                            symbolTable.addSymbol(type, store);
                        }
                    }
                    
                } else if (datatype.equals("lutang")) {
                    match(TokenType.ARITH_OPERATOR);
                    String arithOp = tokens.get(currentTokenIndex - 1).getValue();
                    match(TokenType.NUMBERS);
                    String numbers = tokens.get(currentTokenIndex - 1).getValue();
                    float numbersFloat = 0;
                    
                    // Check if followed by a decimal point (float instead of int)
                    if ((tokens.get(currentTokenIndex).getType()).equals(TokenType.POINT)) {
                        match(TokenType.POINT);
                        match(TokenType.NUMBERS);
                        numbers += "." + tokens.get(currentTokenIndex - 1).getValue();
                        
                        // Convert to float
                        numbersFloat = Float.parseFloat(numbers);
                        
                        // For debugging
//                        System.out.println("tokens.get(currentTokenIndex-3).getValue(): " + tokens.get(currentTokenIndex-3).getValue());
//                        System.out.println("tokens.get(currentTokenIndex-2).getValue(): " + tokens.get(currentTokenIndex-2).getValue());
//                        System.out.println("tokens.get(currentTokenIndex-1).getValue(): " + tokens.get(currentTokenIndex-1).getValue());
                    } else {
                        // Convert to float
                        numbersFloat = Float.parseFloat(numbers);
                        
                        // For debugging
//                        System.out.println("tokens.get(currentTokenIndex-1).getValue(): " + tokens.get(currentTokenIndex-1).getValue());
                    }
                    
                    // compute
                    if (arithOp.equals("lahamz") || arithOp.equals("ghosted")
                            || arithOp.equals("cheater") || arithOp.equals("chariz")) {
                        valuesList.add(numbersFloat);
                        String store = "";
                        
                        if (arithOp.equals("lahamz")) {
                            // Check if left operand is int or not
                            if (((Object) valuesList.get(0)).getClass().getSimpleName().equals("Integer")) {
                                store = Float.toString((Integer) valuesList.get(0) + (Float) valuesList.get(1));
                            } else {
                                store = Float.toString((Float) valuesList.get(0) + (Float) valuesList.get(1));
                            }
                            
                        } else if (arithOp.equals("ghosted")) {
                            // Check if left operand is int or not
                            if (((Object) valuesList.get(0)).getClass().getSimpleName().equals("Integer")) {
                                store = Float.toString((Integer) valuesList.get(0) - (Float) valuesList.get(1));
                            } else {
                                store = Float.toString((Float) valuesList.get(0) - (Float) valuesList.get(1));
                            }
                            
                        } else if (arithOp.equals("cheater")) {
                            // Check if left operand is int or not
                            if (((Object) valuesList.get(0)).getClass().getSimpleName().equals("Integer")) {
                                store = Float.toString((Integer) valuesList.get(0) * (Float) valuesList.get(1));
                            } else {
                                store = Float.toString((Float) valuesList.get(0) * (Float) valuesList.get(1));
                            }
                            
                        } else if (arithOp.equals("chariz")) {
                            // Check if left operand is int or not
                            if (((Object) valuesList.get(0)).getClass().getSimpleName().equals("Integer")) {
                                store = Float.toString((Integer) valuesList.get(0) / (Float) valuesList.get(1));
                            } else {
                                store = Float.toString((Float) valuesList.get(0) / (Float) valuesList.get(1));
                            }
                        }
                        
                        valuesList.clear();
                        valuesList.add(Float.parseFloat(store));

                        String type = "lutang (" + value + ") operations result";
                        
                        // Check if inside a control structure with condition = true
                        // or if not inside a control structure
                        if ((condition[0] && condition[1]) || !condition[0]) {
                            if (symbolTable.containsSymbol(type)) {
                                symbolTable.removeSymbol(type);
                                symbolTable.addSymbol(type, store);
                            } else {
                                symbolTable.addSymbol(type, store);
                            }
                        }
                        
                    } else if (arithOp.equals("dasurv")) {
                        throw new Exception("Float value found in modulo operation");
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
            
        } else if (getCurrentTokenType()
                == TokenType.POINT) {      // Check if float
            match(TokenType.POINT);
            match(TokenType.NUMBERS);
            prevNumbers += "." + tokens.get(currentTokenIndex - 1).getValue();
            
            // Add the float in valuesList
            valuesList.add(Float.parseFloat(prevNumbers));
            
            if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                do {
                    float numbersFloat = 0;
                    
                    match(TokenType.ARITH_OPERATOR);
                    String arithOp = tokens.get(currentTokenIndex - 1).getValue();
                    
                    // Check if identifier
                    if (tokens.get(currentTokenIndex).getType() == TokenType.IDENTIFIER) {
                        value = getCurrentTokenInput();
                        match(TokenType.IDENTIFIER);
                        String numbers = "";
                        
                        // Retrieve the value of the identifier from the symbolTable
                        for (Map.Entry<String, String> entry : symbolTable.getSymbols().entrySet()) {
                            if (entry.getKey().contains("(" + value + ")")) {
                                numbers = entry.getValue();
                                datatype = entry.getKey().substring(0, entry.getKey().indexOf(" "));
                                break;
                            }
                        }
                        
                        // Throw error if id is not in the symbolTable (if idValue is null)
                        if (numbers.equals("")) {
                            throw new Exception("'" + value + "' variable not declared.");
                        }

                        // Convert to float
                        numbersFloat = Float.parseFloat(numbers);
                        
                    } else if (tokens.get(currentTokenIndex).getType()
                            == TokenType.NUMBERS) {     // Check if int or float
                        match(TokenType.NUMBERS);
                        String numbers = tokens.get(currentTokenIndex - 1).getValue();
                        
                        // Check if float
                        if (currentTokenIndex+1 < tokens.size()
                                && (tokens.get(currentTokenIndex+1).getType()).equals(TokenType.POINT)) {
                            match(TokenType.POINT);
                            match(TokenType.NUMBERS);
                            numbers += "." + tokens.get(currentTokenIndex - 1).getValue();

                            // Convert to float
                            numbersFloat = Float.parseFloat(numbers);
                            
                        } else {
                            // Convert to float
                            numbersFloat = Float.parseFloat(numbers);
                            
                        }
                    }
                    
                    // Compute
                    if (arithOp.equals("lahamz") || arithOp.equals("ghosted")
                            || arithOp.equals("cheater") || arithOp.equals("chariz")) {
                        valuesList.add(numbersFloat);
                        String store = "";
                        
                        if (arithOp.equals("lahamz")) {
                            store = Float.toString((Float) valuesList.get(0) + (Float) valuesList.get(1));
                        } else if (arithOp.equals("ghosted")) {
                            store = Float.toString((Float) valuesList.get(0) - (Float) valuesList.get(1));
                        } else if (arithOp.equals("cheater")) {
                            store = Float.toString((Float) valuesList.get(0) * (Float) valuesList.get(1));
                        } else if (arithOp.equals("chariz")) {
                            store = Float.toString((Float) valuesList.get(0) / (Float) valuesList.get(1));
                        }
                        
                        valuesList.clear();
                        valuesList.add(Float.parseFloat(store));

                        String type = "lutang (" + value + ") operations result";
                        
                        // Check if inside a control structure with condition = true
                        // or if not inside a control structure
                        if ((condition[0] && condition[1]) || !condition[0]) {
                            if (symbolTable.containsSymbol(type)) {
                                symbolTable.removeSymbol(type);
                                symbolTable.addSymbol(type, store);
                            } else {
                                symbolTable.addSymbol(type, store);
                            }
                        }
                        
                    } else if (arithOp.equals("dasurv")) {
                        throw new Exception("Float value found in modulo operation");
                    }
                    
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
            String prevNumbers = tokens.get(currentTokenIndex).getValue();
            match(TokenType.NUMBERS);
            
            // Check if float
            if (getCurrentTokenType() == TokenType.POINT) {
                prevNumbers = tokens.get(currentTokenIndex - 1).getValue();
                match(TokenType.POINT);
                match(TokenType.NUMBERS);
                prevNumbers += "." + tokens.get(currentTokenIndex - 1).getValue();
            }
            
            String relOp = tokens.get(currentTokenIndex).getValue();
            match(TokenType.REL_OPERATOR);
            
            // Check if the right operand is an identifier or a numerical value
            TokenType tokenType = getCurrentTokenType();
            if (tokenType == TokenType.IDENTIFIER || tokenType == TokenType.NUMBERS) {                
                // Check if identifier
                if (tokenType == TokenType.IDENTIFIER) {
                    String id = tokens.get(currentTokenIndex).getValue();
                    String value = "";
                    currentTokenIndex++;
                    
                    // Retrieve the value of the identifier from the symbolTable
                    for (Map.Entry<String, String> entry : symbolTable.getSymbols().entrySet()) {
                        if (entry.getKey().contains("(" + id + ")")) {
                            value = entry.getValue();
                            break;
                        }
                    }
                        
                    // compute
                    if (relOp.equals("samedt")
                            && Float.parseFloat(prevNumbers) == Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("diff")
                            && Float.parseFloat(prevNumbers) != Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("W")
                            && Float.parseFloat(prevNumbers) > Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("L")
                            && Float.parseFloat(prevNumbers) < Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("W samedt")
                            && Float.parseFloat(prevNumbers) >= Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("L samedt")
                            && Float.parseFloat(prevNumbers) <= Float.parseFloat(value)) {
                        condition[1] = true;
                    }
                    
                } else {    // numerical value
                    String value = tokens.get(currentTokenIndex).getValue();
                    match(TokenType.NUMBERS);
                    
                    // Check if float
                    if (getCurrentTokenType() == TokenType.POINT) {
                        match(TokenType.POINT);
                        match(TokenType.NUMBERS);
                        value += "." + tokens.get(currentTokenIndex - 1).getValue();
                    }
                    
                    // compute
                    if (relOp.equals("samedt")
                            && Float.parseFloat(prevNumbers) == Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("diff")
                            && Float.parseFloat(prevNumbers) != Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("W")
                            && Float.parseFloat(prevNumbers) > Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("L")
                            && Float.parseFloat(prevNumbers) < Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("W samedt")
                            && Float.parseFloat(prevNumbers) >= Float.parseFloat(value)) {
                        condition[1] = true;
                    } else if (relOp.equals("L samedt")
                            && Float.parseFloat(prevNumbers) <= Float.parseFloat(value)) {
                        condition[1] = true;
                    }
                }
            }
            
            if (getCurrentTokenType() == TokenType.LOGIC_OPERATOR) {
                parseLogicalOp();
            }
        } else if (getCurrentTokenType() == TokenType.IDENTIFIER) {
            String leftId = tokens.get(currentTokenIndex).getValue();
            String prevNumbers = "";
            match(TokenType.IDENTIFIER);
            
            // Retrieve the value of the identifier from the symbolTable
            for (Map.Entry<String, String> entry : symbolTable.getSymbols().entrySet()) {
                if (entry.getKey().contains("(" + leftId + ")")) {
                    prevNumbers = entry.getValue();
                    break;
                }
            }
            
            if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
                String relOp = tokens.get(currentTokenIndex).getValue();
                match(TokenType.REL_OPERATOR);
                
                // Check if the right operand is an identifier or a numerical value
                TokenType tokenType = getCurrentTokenType();
                if (tokenType == TokenType.IDENTIFIER || tokenType == TokenType.NUMBERS) {                
                    // Check if identifier
                    if (tokenType == TokenType.IDENTIFIER) {
                        String rightId = tokens.get(currentTokenIndex).getValue();
                        String value = "";
                        currentTokenIndex++;

                        // Retrieve the value of the identifier from the symbolTable
                        for (Map.Entry<String, String> entry : symbolTable.getSymbols().entrySet()) {
                            if (entry.getKey().contains("(" + rightId + ")")) {
                                value = entry.getValue();
                                break;
                            }
                        }
                        
                        // compute
                        if (relOp.equals("samedt")
                                && Float.parseFloat(prevNumbers) == Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("diff")
                                && Float.parseFloat(prevNumbers) != Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("W")
                                && Float.parseFloat(prevNumbers) > Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("L")
                                && Float.parseFloat(prevNumbers) < Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("W samedt")
                                && Float.parseFloat(prevNumbers) >= Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("L samedt")
                                && Float.parseFloat(prevNumbers) <= Float.parseFloat(value)) {
                            condition[1] = true;
                        }
                    } else {    // numerical value
                        String value = tokens.get(currentTokenIndex).getValue();
                        match(TokenType.NUMBERS);
                    
                        // Check if float
                        if (getCurrentTokenType() == TokenType.POINT) {
                            match(TokenType.POINT);
                            match(TokenType.NUMBERS);
                            value += "." + tokens.get(currentTokenIndex - 1).getValue();
                        }
                        
                        // compute
                        if (relOp.equals("samedt")
                                && Float.parseFloat(prevNumbers) == Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("diff")
                                && Float.parseFloat(prevNumbers) != Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("W")
                                && Float.parseFloat(prevNumbers) > Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("L")
                                && Float.parseFloat(prevNumbers) < Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("W samedt")
                                && Float.parseFloat(prevNumbers) >= Float.parseFloat(value)) {
                            condition[1] = true;
                        } else if (relOp.equals("L samedt")
                                && Float.parseFloat(prevNumbers) <= Float.parseFloat(value)) {
                            condition[1] = true;
                        }
                    }
                }
                
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
        
        // For debugging
//        System.out.println("tokens.get(currentTokenIndex-1).getValue(): " + tokens.get(currentTokenIndex-1).getValue());
//        System.out.println("tokens.get(currentTokenIndex).getValue(): " + tokens.get(currentTokenIndex).getValue());
        
        if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
            parseArithOp("");
        }

        if (getCurrentTokenType() == TokenType.REL_OPERATOR) {
            match(TokenType.REL_OPERATOR);
            if (getCurrentTokenType() == TokenType.IDENTIFIER
                    || getCurrentTokenType() == TokenType.NUMBERS) { // check if variable or value
                TokenType prevTokenType = tokens.get(currentTokenIndex-1).getType();
                currentTokenIndex++;
                
                // Check if float
                if (prevTokenType == TokenType.NUMBERS && getCurrentTokenType() == TokenType.POINT) {
                    match(TokenType.POINT);
                    match(TokenType.NUMBERS);
                }
            }
        }
    }

    private void parseVariableName() throws Exception {
        match(TokenType.IDENTIFIER);
    }

    private void parseKeyword() throws Exception {
        if (getCurrentTokenInput().equals("tbh")) {    // if / if-else chain / if-else statement
            String leftOperand = "";
            String rightOperand = "";
            
            match(TokenType.KEYWORD);
            match(TokenType.OPENPARENTHESIS);
            // Check if the left operand is a variable
            if (getCurrentToken().getType().equals(TokenType.IDENTIFIER)) {
                leftOperand = getCurrentToken().getValue();
            }
            checkRelorLog();
            // Check if the right operand is a variable
            if (tokens.get(currentTokenIndex-1).getType().equals(TokenType.IDENTIFIER)) {
                rightOperand = tokens.get(currentTokenIndex-1).getValue();
            }
            match(TokenType.CLOSEPARENTHESIS);
            
            // For debugging
//            System.out.println("leftOperand: " + leftOperand);
//            System.out.println("rightOperand: " + rightOperand);
            
            // Check if any variable inside the condition has NOT been declared
            if (!leftOperand.equals("") || !rightOperand.equals("")) {
                if (!leftOperand.equals("") && !symbolTable.containsSymbol(leftOperand)) {
                    throw new Exception("variable " + leftOperand + " has not been declared before the control structure");
                } else if (!rightOperand.equals("") && !symbolTable.containsSymbol(rightOperand)) {
                    throw new Exception("variable " + rightOperand + " has not been declared before the control structure");
                }
            }
            
            match(TokenType.OPENBRACE);
            
            // Check if the condition of the if-statement is true
            condition[0] = true;
            while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                parseStatement();
            }
            
            condition[0] = false;
            condition[1] = false;
            
            match(TokenType.CLOSEBRACE);
            
            // check for else-if statement/s
            if (currentTokenIndex < tokens.size()) {
                while (getCurrentTokenInput().equals("nvm tbh")) {
                    match(TokenType.KEYWORD);
                    
                    match(TokenType.OPENPARENTHESIS);
                    checkRelorLog();
                    match(TokenType.CLOSEPARENTHESIS);
                    
                    match(TokenType.OPENBRACE);
                    while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                        parseStatement();
                    }
                    match(TokenType.CLOSEBRACE);
                }

                // check for "else" statement
                if (getCurrentTokenInput().equals("nvm")) { // else statement
                    match(TokenType.KEYWORD);
                    
                    match(TokenType.OPENBRACE);
                    while (!getCurrentTokenType().equals(TokenType.CLOSEBRACE)) {
                        parseStatement();   // statement/s
                    }
                    match(TokenType.CLOSEBRACE);
                }
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
            
            // Check if float
            if (getCurrentTokenType() == TokenType.POINT) {
                match(TokenType.POINT);
                match(TokenType.NUMBERS);
            }
            
            // Check if arith op
            if (getCurrentTokenType() == TokenType.ARITH_OPERATOR) {
                currentTokenIndex -= 2;
                parseArithOp("");
            }
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
