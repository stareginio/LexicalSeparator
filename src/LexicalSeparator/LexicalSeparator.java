package LexicalSeparator;

import java.util.*;

public class LexicalSeparator {

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String input = s.nextLine();

        List<Token> tokens = analyze(input);
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public static List<Token> analyze(String input) {
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
                tokens.add(new Token(TokenType.NUMBER, sb.toString()));
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
                if (value.equals("digits") || value.equals("lutang") || value.equals("yarn") || value.equals("awit")) {
                    tokens.add(new Token(TokenType.DATATYPE, value));
                } else if (value.equals("lahamz") || value.equals("ghosted") || value.equals("cheater") || value.equals("chariz")) {
                    tokens.add(new Token(TokenType.ARITHOPERATOR, value));
                } else {
                    tokens.add(new Token(TokenType.VARIABLENAME, value));
                }
            } else if (c == '=') {
                // parse assignment or comparison operator
                if (i + 1 < input.length() && input.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.COMPARISON_OPERATOR, "=="));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.ASSIGNMENT_OPERATOR, "="));
                    i++;
                }
            } else if (c == '\'') {
                // parse string literal
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < input.length() && input.charAt(i) != '\'') {
                    sb.append(input.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.STRING_LITERAL, sb.toString()));
                i++;
            } else {
                // unknown character
                throw new IllegalArgumentException("Unknown character: " + c);
            }
        }
        return tokens;
    }
}