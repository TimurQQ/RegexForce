package ru.mephi.timurq;

import ru.mephi.timurq.automaton.DFA;
import ru.mephi.timurq.automaton.DFAGraphics;
import ru.mephi.timurq.lang.BasicOperations;
import ru.mephi.timurq.regex.Matcher;
import ru.mephi.timurq.regex.Pattern;
import ru.mephi.timurq.regex.RegExp;
import ru.mephi.timurq.syntaxtree.SyntaxTree;

import java.io.IOException;

public class Testing {

    public static void main(String[] args) throws IOException {
        RegExp regex = new RegExp("(ab|aa|b|ca|ddddd)(c|d|cd)");//RegExp("(a|b)*c");
        //System.out.println(regex);
        SyntaxTree sTree = new SyntaxTree(regex);
        sTree.printData();
        System.out.println(sTree.getTmpGroups());
        DFA automaton = new DFA(regex);
        new DFAGraphics(automaton, "");
        System.out.println(automaton.getGroups("abcd"));
//        RegExp regex1 = new RegExp("((ab)*)c*");
//        //System.out.println(regex1);
//        SyntaxTree sTree1 = new SyntaxTree(regex1);
//        System.out.println(sTree1.getTmpGroups());
//        sTree1.printData();
//        DFA automaton1 = new DFA(regex1);
//        new DFAGraphics(automaton1, "");
//        System.out.println(automaton1.getGroups("abababababccc"));
        //sTree.print();
//        Pattern pattern = new Pattern(regex);
//        pattern.compile();
//        DFA automaton = pattern.getDFAObject();
//        //automaton.printTable();
//        Matcher matcher = new Matcher("bbacaaaaabbac", automaton);
//        System.out.println(matcher.find());
//        System.out.println(matcher.group());
//        System.out.println(matcher.find());
//        System.out.println(matcher.group());
//        matcher.findAll();
//        System.out.println(matcher.groupCount());
//        System.out.println(matcher.group(0));
//        System.out.println(matcher.group(1));
        String reString = "((ab|b(bb|a)))*";
        DFA langDFA = new DFA(reString);
        String guessedRegex = BasicOperations.getRegexFromAutomata(langDFA);
        System.out.println(guessedRegex);
    }
}
