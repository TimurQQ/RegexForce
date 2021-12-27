package ru.mephi.timurq.automaton;

import java.util.*;

public class TransitionTable {

    private Map<DFAState, Transition> table = new HashMap<>();

    protected void addTransition(Transition t) {
        if (table.containsKey(t.getState())) {
            table.get(t.getState()).addTransition(t);
        } else {
            table.put(t.getState(), t);
        }
    }

    protected Map<Character, Set<DFAState>> getPossibleTransitions(DFAState state) {
        if (table.containsKey(state))
            return table.get(state).moves;
        else
            return new HashMap<>();
    }

    protected DFAState getGoalFromTransition(DFAState start, Character c) {
        return getPossibleTransitions(start).get(c).iterator().next();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<DFAState> states = new ArrayList<>(table.keySet());
        for (DFAState s : states) {
            sb.append(table.get(s)).append("\n     ");
        }
        return sb.toString();
    }
}
