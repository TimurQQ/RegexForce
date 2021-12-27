package ru.mephi.timurq.automaton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Transition {
    private final DFAState state;
    protected Map<Character, Set<DFAState>> moves = new HashMap<>();

    public DFAState getState() {
        return state;
    }

    public Transition(DFAState state, Character c, Set<DFAState> states) {
        this.state = state;
        moves.put(c, states);
    }

    public void addMove(Character c, Set<DFAState> states) {
        if (!moves.containsKey(c))
            moves.put(c, states);
        else
            moves.get(c).addAll(states);
    }

    public void addTransition(Transition t) {
        for (Map.Entry<Character, Set<DFAState>> entry : t.moves.entrySet()) {
            if (!moves.containsKey(entry.getKey())) {
                moves.put(entry.getKey(), entry.getValue());
            } else {
                moves.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Set<DFAState>> move : moves.entrySet())
            sb.append(String.format("[%s] x '%s' → %s\t", state, move.getKey(), Arrays.toString(move.getValue().toArray())));
        return sb.toString().trim();
    }
}
