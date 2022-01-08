package ru.mephi.timurq.lang;

import ru.mephi.timurq.automaton.*;
import java.util.*;

public class BasicOperations {
    static public DFA minus(DFA a1, DFA a2) {
        if (a1.isEmpty() || a1 == a2)
            return new DFA();
        if (a2.isEmpty())
            return a1;
        Set<String> newAlpha = new HashSet<>();
        newAlpha.addAll(a1.getAlphabet());
        newAlpha.addAll(a2.getAlphabet());
        return intersection(a1.getTotal(newAlpha), a2.getComplement(newAlpha).minimize());
    }

    static public DFA intersection(DFA a1, DFA a2) {
        if (a1 == a2)
            return a1;
        return dfaIntersection(a1, a2);
    }

    static private DFA dfaIntersection(DFA a1, DFA a2) {
        DFA c = new DFA();
        c.addToAlphabet(a1.getAlphabet());
        DFAState.resetIds();
        List<StatePair> generatedStates = new ArrayList<>();
        StatePair startStatePair = new StatePair(a1.getStartState(), a2.getStartState());
        c.addState(DFAState.mergePair(startStatePair));
        generatedStates.add(new StatePair(a1.getStartState(), a2.getStartState()));
        for (int i = 0; i < generatedStates.size(); ++i) {
            StatePair curStatePair = generatedStates.get(i);
            Set<DFAState> curStateSet = Set.of(curStatePair.getFirstState(), curStatePair.getSecondState());
            DFAState start = findStateWithIncludePair(curStateSet, new HashSet<>(c.getStates()));
            for (String sym : c.getAlphabet()) {
                int index1 = a1.getStates().indexOf(curStatePair.getFirstState());
                int index2 = a2.getStates().indexOf(curStatePair.getSecondState());
                DFAState goal1 = a1.getStates().get(index1).getTransition(sym);
                DFAState goal2 = a2.getStates().get(index2).getTransition(sym);
                index1 = a1.getStates().indexOf(goal1);
                index2 = a2.getStates().indexOf(goal2);
                goal1 = a1.getStates().get(index1);
                goal2 = a2.getStates().get(index2);
                StatePair newStatePair = new StatePair(goal1, goal2);
                Set<DFAState> newStateSet = Set.of(goal1, goal2);
                if (!generatedStates.contains(newStatePair)) {
                    c.addState(DFAState.mergePair(newStatePair));
                    generatedStates.add(newStatePair);
                }
                DFAState goalC = findStateWithIncludePair(newStateSet, new HashSet<>(c.getStates()));
                c.addTransitionToTable(new Transition(start, sym, goalC.asSet()));
                start.addTransition(goalC, sym);
            }
        }
        c.renameStates();
        c.minimize();
        return c;
    }

    private static DFAState findStateWithIncludePair(Set<DFAState> s, Set<DFAState> states) {
        DFAState result = null;
        for (DFAState curr : states) {
            if (curr.getIncludedStates().containsAll(s)) {
                result = curr;
                break;
            }
        }
        return result;
    }

}
