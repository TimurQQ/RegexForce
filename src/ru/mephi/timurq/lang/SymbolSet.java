package ru.mephi.timurq.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolSet {
    private final List<Character> symbolSet = new ArrayList<>();

    public SymbolSet() {
        for (char ch = 'A'; ch <= 'Z'; ++ch) {
            symbolSet.add(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            symbolSet.add(ch);
        }
        Collections.addAll(symbolSet, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        Collections.addAll(symbolSet, '#', '\\', '=', '_', '.', '*', '/', '+', '-', ' ', '(', ')');
    }

    public boolean contains(char c) {
        return symbolSet.contains(c);
    }
}
