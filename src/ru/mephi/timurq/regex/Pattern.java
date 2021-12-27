package ru.mephi.timurq.regex;

import ru.mephi.timurq.automaton.DFA;
import ru.mephi.timurq.syntaxtree.SyntaxTree;

public class Pattern {
    private final RegExp regexp;
    private final SyntaxTree sTree;
    private DFA genDFA;

    public Pattern(RegExp r) {
        regexp = r;
        sTree = new SyntaxTree(regexp);
        genDFA = null;
    }

    public Pattern(String rString) {
        regexp = new RegExp(rString);
        sTree = new SyntaxTree(regexp);
        genDFA = null;
    }

    public void compile() {
        genDFA = new DFA(regexp, sTree);
    }

    public DFA getDFAObject() {
        return genDFA;
    }
}
