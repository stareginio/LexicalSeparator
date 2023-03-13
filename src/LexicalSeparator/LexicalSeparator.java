package LexicalSeparator;

import java.io.*;
import java.util.*;

public class LexicalSeparator {

    public static void main(String[] args) {
        File file = new File("input.txt");
        
        String fileName = file.getName();
        List<String> input = new ArrayList<String>();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int ctr = 0;
            String line;
            
            while ((line = br.readLine()) != null) {
                input.add(line);
                // System.out.println("line " + ctr + ": " + line);
                ctr++;
            }
            
            br.close();
            
            List<Token> output = new ArrayList<>();
            
            for (String str : input) {
                List<Token> tokens = analyze(str);
                
                for (Token token : tokens) {
                    System.out.println(token);
                    output.add(token);
                }
            }
            
            // Display Output Table
            new OutputTable(output);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Token> analyze(String input) {

        List<Character> symbols = Arrays.asList('~', '!', '@', '#', '$', '%', 
                '^', '&', '*', '-', '_', '+', '[', ']', '|', '\\', '/', ':', 
                ';', '<', '>', ',', '.', '?');

        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);
            if (Character.isWhitespace(c)) {
                // skip whitespace
                i++;
            } else if (Character.isDigit(c)) {
                // parse number
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    sb.append(input.charAt(i));
                    i++;
                }

                tokens.add(new Token(TokenType.NUMBERS, sb.toString()));
            } else if (c == '-') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                if (Character.isDigit(input.charAt(i))) {
                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        sb.append(input.charAt(i));
                        i++;
                    }
                    tokens.add(new Token(TokenType.NUMBERS, sb.toString()));
                } else {
                    tokens.add(new Token(TokenType.SYMBOL, sb.toString()));
                }
            } else if (c == ',') {
                 // parse operator
                tokens.add(new Token(TokenType.COMMA, Character.toString(c)));
                i++;
            } else if (Character.isLetter(c)) {
                // parse identifier or keyword
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                while (i < input.length() && Character.isLetterOrDigit(input.charAt(i))) {
                    sb.append(input.charAt(i));
                    i++;
                }
                String value = sb.toString();
                if (value.equals("digits") || value.equals("lutang")
                        || value.equals("yarn") || value.equals("awit")) {
                    tokens.add(new Token(TokenType.DATATYPE, value));
                } else if (value.equals("lahamz") || value.equals("ghosted") || value.equals("cheater")
                        || value.equals("chariz") || value.equals("dasurv")) {
                    tokens.add(new Token(TokenType.ARITH_OPERATOR, value));
                } else if ((value.equals("W") || value.equals("L"))) {
                    // parse the next word
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(" ");
                    int ctr = 1;
                    i++;
                    while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)))) {
                        sb2.append(input.charAt(i));
                        i++;
                        ctr++;
                    }
                    String value2 = value + sb2.toString();

                    if (value2.equals("W samedt") || value2.equals("L samedt")) {
                        tokens.add(new Token(TokenType.REL_OPERATOR, value2));
                    } else {    // if "samedt" is not detected
                        tokens.add(new Token(TokenType.REL_OPERATOR, value));
                        i -= ctr;
                    }
                } else if (value.equals("samedt") || value.equals("diff")) {
                    tokens.add(new Token(TokenType.REL_OPERATOR, value));
                } else if (value.equals("naol") || value.equals("edewups") || value.equals("naur")) {
                    tokens.add(new Token(TokenType.LOGIC_OPERATOR, value));
                } else if ((value.equals("nvm") || value.equals("vibe"))) {
                    // parse the next word
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(" ");
                    int ctr = 1;
                    i++;
                    while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)))) {
                        sb2.append(input.charAt(i));
                        i++;
                        ctr++;
                    }
                    String value2 = value + sb2.toString();
                    
                    if (value2.equals("nvm tbh") || value2.equals("vibe check")) {
                        tokens.add(new Token(TokenType.KEYWORD, value2));
                    } else if (value.equals("nvm")) {   // if "tbh" is not detected for "nvm"
                        tokens.add(new Token(TokenType.KEYWORD, value));
                        i -= ctr;
                    } else {    // if only "vibe" is provided
                        tokens.add(new Token(TokenType.IDENTIFIER, value));
                        i -= ctr;
                    }
                } else if (value.equals("tbh") || value.equals("g") || value.equals("forda")) {
                    tokens.add(new Token(TokenType.KEYWORD, value));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, value));
                }
            } else if (c == ';') {
                // parse operator
                tokens.add(new Token(TokenType.SEMICOLON, Character.toString(c)));
                i++;
            } else if (c == '(') {
                // parse operator
                tokens.add(new Token(TokenType.OPENPARENTHESIS, Character.toString(c)));
                i++;
            } else if (c == ')') {
                // parse operator
                tokens.add(new Token(TokenType.CLOSEPARENTHESIS, Character.toString(c)));
                i++;
            } else if (c == '[') {
                // parse operator
                tokens.add(new Token(TokenType.OPENBRACKET, Character.toString(c)));
                i++;
            } else if (c == ']') {
                // parse operator
                tokens.add(new Token(TokenType.CLOSEBRACKET, Character.toString(c)));
                i++;
            } else if (c == '{') {
                // parse operator
                tokens.add(new Token(TokenType.OPENBRACE, Character.toString(c)));
                i++;
            } else if (c == '}') {
                // parse operator
                tokens.add(new Token(TokenType.CLOSEBRACE, Character.toString(c)));
                i++;
            } else if (c == '=') {
                // parse assignment or comparison operator
                if (i + 1 < input.length() && input.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.COMPARISON_OPERATOR, "=="));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.ASSIGNMENT_OPERATOR, "="));
                    i++;
                }
            } else if (c == '"') {
                // parse string literal
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                while (i < input.length()) {
                    sb.append(input.charAt(i));
                    i++;
                    if (input.charAt(i) == '"') {
                         sb.append(input.charAt(i));
                        break;
                    }
                }
                tokens.add(new Token(TokenType.STRING_LITERAL, sb.toString()));
                i++;
            } else if (symbols.contains(c)) {
                tokens.add(new Token(TokenType.SYMBOL, Character.toString(c)));
                i++;
            } else {
                // unknown character
                throw new IllegalArgumentException("Unknown character: " + c);
            }
        }
        return tokens;
    }
}