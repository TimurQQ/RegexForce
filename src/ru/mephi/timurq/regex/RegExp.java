package ru.mephi.timurq.regex;

import ru.mephi.timurq.lang.OperatorsSet;
import ru.mephi.timurq.lang.SymbolSet;

public class RegExp {
    private final String initialValue;
    private final String preparedRegex;
    private final SymbolSet symbolSet = new SymbolSet();
    private final OperatorsSet opSet = new OperatorsSet();

    public RegExp(String s) {
        initialValue = s;
        preparedRegex = prepare(initialValue);
    }

    private String prepare(String initialRegex) {
        StringBuilder newRegex = fillConcatenations(initialRegex);
        return newRegex.toString();
    }

    private StringBuilder fillConcatenations(String initialRegex) {
        StringBuilder modernRegex = new StringBuilder();
        modernRegex.append("(");
        for (int i = 0; i < initialRegex.length() - 1; ++i) {
            boolean escapedBrace = initialRegex.charAt(i) == '(' && i > 0 && initialRegex.charAt(i - 1) == '\\';
            if (initialRegex.charAt(i) == '\\' && isSymbol(initialRegex.charAt(i + 1))) {
                modernRegex.append(initialRegex.charAt(i));
            } else if (initialRegex.charAt(i) == '\\' && initialRegex.charAt(i + 1) == '(') {
                modernRegex.append(initialRegex.charAt(i));
            } else if ((isSymbol(initialRegex.charAt(i)) || (escapedBrace)) && isSymbol(initialRegex.charAt(i + 1))) {
                modernRegex.append(initialRegex.charAt(i)).append('&');
            } else if ((isSymbol(initialRegex.charAt(i)) || (escapedBrace)) && initialRegex.charAt(i + 1) == '(') {
                modernRegex.append(initialRegex.charAt(i)).append('&');
            } else if (initialRegex.charAt(i) == ')' && isSymbol(initialRegex.charAt(i + 1))) {
                modernRegex.append(initialRegex.charAt(i)).append('&');
            } else if (initialRegex.charAt(i) == '*' && isSymbol(initialRegex.charAt(i + 1))) {
                modernRegex.append(initialRegex.charAt(i)).append('&');
            } else if (initialRegex.charAt(i) == '*' && initialRegex.charAt(i + 1) == '(') {
                modernRegex.append(initialRegex.charAt(i)).append('&');
            } else if (initialRegex.charAt(i) == ')' && initialRegex.charAt(i + 1) == '(') {
                modernRegex.append(initialRegex.charAt(i)).append('&');
            } else if (initialRegex.charAt(i) == '[') {
                i++;
                int from = initialRegex.charAt(i);
                i = i + 2;
                int to = initialRegex.charAt(i);
                modernRegex.append('(');
                modernRegex.append((char) from++);
                while (from <= to) {
                    modernRegex.append('|').append((char) from++);
                }
            } else if (initialRegex.charAt(i) == ']') {
                System.out.println("HUY");
                System.out.println(i);
                if (isSymbol(initialRegex.charAt(i + 1)) || initialRegex.charAt(i + 1) == '[' || initialRegex.charAt(i + 1) == '(') {
                    modernRegex.append(")&");
                } else {
                    modernRegex.append(')');
                }
            } else {
                modernRegex.append(initialRegex.charAt(i));
            }
        }
        if (initialRegex.charAt(initialRegex.length() - 1) == ']') {
            modernRegex.append(')');
        } else {
            modernRegex.append(initialRegex.charAt(initialRegex.length() - 1));
        }
        modernRegex.append(")&#");
        System.out.println(initialRegex);
        System.out.println(modernRegex);
        return modernRegex;
    }

    private boolean isSymbol(char ch) {
        return symbolSet.contains(ch) && !opSet.contains(ch);
    }

    @Override
    public String toString() {
        return preparedRegex;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public SymbolSet getSymbolSet() {
        return symbolSet;
    }

    public OperatorsSet getOpSet() {
        return opSet;
    }
}
