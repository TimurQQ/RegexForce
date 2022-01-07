package ru.mephi.timurq;

import ru.mephi.timurq.automaton.DFA;
import ru.mephi.timurq.regex.Matcher;
import ru.mephi.timurq.regex.Pattern;
import ru.mephi.timurq.regex.RegExp;
import ru.mephi.timurq.syntaxtree.SyntaxTree;

public class Testing {

    public static void main(String[] args) {
        RegExp regex = new RegExp("(a|b)*c");
        //System.out.println(regex);
        SyntaxTree sTree = new SyntaxTree(regex);
        //sTree.print();
        Pattern pattern = new Pattern(regex);
        pattern.compile();
        DFA automaton = pattern.getDFAObject();
        //automaton.printTable();
        Matcher matcher = new Matcher("bbacaaaaabbac", automaton);
        System.out.println(matcher.find());
        System.out.println(matcher.group());
        System.out.println(matcher.find());
        System.out.println(matcher.group());
    }
}
