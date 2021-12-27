package ru.mephi.timurq.automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DFAState {
    private int id;
    private static int nextID = 0;
    private String nickname = null;
    private Set<Integer> name = null;
    private final Map<Character, DFAState> transitions = new HashMap<>();
    private Set<DFAState> includedStates = new HashSet<>();
    private Set<Integer> includedIds = new HashSet<>();
    private boolean isStartState = false;
    private boolean isFinalState = false;
    private boolean isTrap = false;
    private boolean marked = false;

    public DFAState(Set<Integer> name) {
        this.name = name;
    }

    public DFAState(String nickname, boolean isInitial, boolean isFinal, Set<DFAState> includedStates) {
        this(nickname);
        this.isStartState = isInitial;
        this.isFinalState = isFinal;
        this.includedStates.addAll(includedStates);
    }

    public DFAState(String nickname, Set<Integer> includedIds) {
        this(nickname);
        this.includedIds = includedIds;
    }

    public DFAState(String nickname) {
        this.nickname = nickname;
        this.id = getNextID();
    }

    public DFAState() {
        name = new HashSet<>();
    }

    public Set<DFAState> getIncludedStates() {
        return includedStates;
    }

    public Set<Integer> getName() {
        return name;
    }

    public void addTransition(DFAState dState, char sym) {
        transitions.put(sym, dState);
    }

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public String getNickname() {
        return nickname;
    }

    public Set<DFAState> asSet() {
        Set<DFAState> result = new HashSet<>();
        result.add(this);
        return result;
    }

    private static int getNextID() {
        return nextID++;
    }

    public Map<Character, DFAState> getTransitions() {
        return transitions;
    }

    public boolean isStart() {
        return isStartState;
    }

    public boolean isFinal() {
        return isFinalState;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public void setStart() {
        isStartState = true;
    }

    public void setFinal() {
        isFinalState = true;
    }

    public void setTrap() {
        isTrap = true;
    }

    public boolean isTrap() {
        return isTrap;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        else if (!(o instanceof DFAState)) return false;
        else {
            DFAState temp = (DFAState) o;
            if (this.name != null && temp.name != null) {
                return this.name.equals(temp.getName());
            } else {
               return this.nickname != null && this.nickname.equals(temp.nickname);
            }
        }
    }

    public DFAState getTransition(char sym) {
        return transitions.get(sym);
    }

    public boolean existsTransitions(char x) {
        return transitions.containsKey(x);
    }

    public static <T> DFAState mergeStates(Set<T> states) {
        StringBuilder sb = new StringBuilder();
        boolean neuIsStart = false;
        boolean neuIsFinal = false;
        Set<DFAState> includedStates = new HashSet<>();
        Set<Integer> includedIds = new HashSet<>();
        for (T o : states) {
            if (o instanceof DFAState) {
                DFAState state = (DFAState) o;
                if (state.isStart())
                    neuIsStart = true;
                if (state.isFinal())
                    neuIsFinal = true;
                sb.append(state);
                includedStates.add(state);
            } else if (o instanceof Integer) {
                Integer id = (Integer) o;
                includedIds.add(id);
                sb.append(id);
            }
        }

        DFAState neu;
        if (includedStates.size() > 0)
            neu = new DFAState(sb.toString(), neuIsStart, neuIsFinal, includedStates);
        else
            neu = new DFAState(sb.toString(), includedIds);
        System.out.println(neu);
        return neu;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isStart()) {
            sb.append("->");
        } else if (this.isFinal()) {
            sb.append("F_");
        }
        sb.append("Q").append(this.getId()).append("(").append(this.getName()).append("):");
        sb.append("Nick: ").append(nickname);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id ^ (id >>> 32));
        return result;
    }
}
