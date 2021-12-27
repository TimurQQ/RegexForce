package ru.mephi.timurq;

import ru.mephi.timurq.automaton.DFA;
import ru.mephi.timurq.regex.Pattern;
import ru.mephi.timurq.regex.RegExp;
import ru.mephi.timurq.syntaxtree.SyntaxTree;

public class Testing {

    public static void main(String[] args) {
        RegExp regex = new RegExp("(a|b)*a");
        System.out.println(regex);
        SyntaxTree sTree = new SyntaxTree(regex);
        sTree.print();
        Pattern pattern = new Pattern(regex);
        pattern.compile();
        DFA automaton = pattern.getDFAObject();
        automaton.printTable();
    }
}
