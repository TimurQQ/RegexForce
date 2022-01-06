package ru.mephi.timurq.automaton;

import ru.mephi.timurq.lang.OperatorsSet;
import ru.mephi.timurq.lang.SymbolSet;
import ru.mephi.timurq.regex.RegExp;
import ru.mephi.timurq.syntaxtree.LeafNode;
import ru.mephi.timurq.syntaxtree.SyntaxTree;

import java.util.*;

public class DFA {
    protected TransitionTable lambda = new TransitionTable();
    private final List<DFAState> listStates = new ArrayList<>();
    private final Set<String> alphabet = new HashSet<>();
    private final SymbolSet symbolSet = new SymbolSet();
    private SyntaxTree sTree = null;
    private RegExp regex = null;
    private final OperatorsSet opSet = new OperatorsSet();
    private DFA dfaMin = null;

    public DFA(RegExp regex, SyntaxTree sTree) {
        this.regex = regex;
        this.sTree = sTree;

        this.generateAlphabet(regex.getInitialValue());
        this.generateTransitions();
        this.generateDFA();
    }

    public DFA(RegExp regex) {
        this.regex = regex;
        sTree = new SyntaxTree(regex);

        this.generateAlphabet(regex.getInitialValue());
        this.generateTransitions();
        this.generateDFA();
    }

    public DFA(String rString) {
        regex = new RegExp(rString);
        sTree = new SyntaxTree(regex);

        this.generateAlphabet(regex.getInitialValue());
        this.generateTransitions();
        this.generateDFA();
    }

    public DFA() {
    }

    private void generateAlphabet(String str) {
        // Flag which is true when there is something like: \( or \* or etc
        boolean isEscaped = false;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\\' && !isEscaped) {
                isEscaped = true;
                continue;
            }
            if (isSymbol(str.charAt(i)) || isEscaped) {
                if (isEscaped) {
                    //create a node with "\{symbol}" symbol
                    alphabet.add("\\" + str.charAt(i));
                }
                else{
                    alphabet.add(Character.toString(str.charAt(i)));
                }
                isEscaped = false;
            }
            else if (str.charAt(i) == '[') {
                i++;
                char tem = str.charAt(i);
                i = i + 2;
                char tem2 = str.charAt(i);
                while (tem <= tem2) {
                    alphabet.add(Character.toString(tem));
                    tem++;
                }
            }
        }
    }

    private void generateTransitions() {
        DFAState start = new DFAState(sTree.getRoot().getFirstPos());
        start.setStart();
        listStates.add(start);
        int i = 0;
        int size = listStates.size();
        listStates.get(i).setId(i);
        while (i < size) {
            for (String sym : alphabet) {
                Set<Integer> temp = this.nextStateName(listStates.get(i).getName(), sym);
                DFAState ds = new DFAState(temp);
                if (!listStates.contains(ds)) {
                    listStates.add(ds);
                    listStates.get(listStates.size() - 1).setId(listStates.size() - 1);
                } else {
                    int in = listStates.indexOf(ds);
                    ds.setId(in);
                }
                listStates.get(i).addTransition(ds, sym);
                Transition t = new Transition(listStates.get(i), sym, ds.asSet());
                lambda.addTransition(t);
            }
            i++;
            size = listStates.size();
        }
    }

    private boolean isSymbol(char ch) {
        return symbolSet.contains(ch) && !opSet.contains(ch);
    }

    private Set<Integer> nextStateName(Set<Integer> cur, String sym) {
        Set<Integer> temp = new HashSet<>();
        for (int i : cur) {
            if (Objects.requireNonNull(this.getLeaf(i)).getSymbol().equals(sym))
                temp.addAll(Objects.requireNonNull(this.getLeaf(i)).getFollowPos());
        }
        return temp;
    }

    private LeafNode getLeaf(int id) {
        for (LeafNode temp : sTree.getLeaves()) {
            if (temp.getId() == id) return temp;
        }
        return null;
    }

    public void addToAlphabet(String ch) {
        alphabet.add(ch);
    }

    public void addToAlphabet(Collection<String> alpha) {
        alphabet.addAll(alpha);
    }

    public void addState(DFAState state) {
        this.getStates().add(state);
    }

    public void printTable() {
        for (DFAState ds : listStates) {
            if (ds.isStart()) {
                System.out.print("->");
            } else if (ds.isFinal()) {
                System.out.print("F_");
            }
            System.out.print("Q" + ds.getId() + "(" + ds.getName() + "):\r\n");
            for (String sym : alphabet) {
                if (ds.existsTransitions(sym)) {
                    System.out.println("D(Q" + ds.getId() + "," + sym + ")= " + "Q" + ds.getTransition(sym).getId() + "(" + ds.getTransition(sym).getName() + ")");
                }
            }
        }
    }

    public void isValidString(String str) {
        int index = 0, i, tmpIndex;
        for (i = 0; i < str.length(); i++) {
            DFAState temp = listStates.get(index).getTransition(Character.toString(str.charAt(i)));
            tmpIndex = index;
            index = listStates.indexOf(temp);
            System.out.println("Q" + listStates.get(tmpIndex).getId() + "," + str.charAt(i) + " -> Q" + listStates.get(index).getId());
            if (i == str.length() - 1 && listStates.get(index).isFinal()) {
                System.out.println("STRING ACCEPTED");
                return;
            }
        }
        System.out.println("STRING REJECTED");
    }

    public List<DFAState> getStates() {
        return listStates;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    private void generateDFA() {
        int last = sTree.getLeaves().size();
        for (DFAState ds : listStates) {
            if (ds.getName().contains(last)) ds.setFinal();
            int i = 0;
            for (String sym : alphabet) {
                if (ds.getTransition(sym).equals(ds)) i++;
            }
            if (i == alphabet.size() && !ds.isFinal()) ds.setTrap();
        }
    }

    public DFA minimize() {

        if (dfaMin != null) return dfaMin;
        System.out.println("[*] Building groups\n");

        List<Set<DFAState>> groups = buildGroups();
        System.out.println(groups);

        System.out.println("[*] Groups built\n");
        System.out.println("[*] Building DFA_min\n");
        dfaMin = new DFA();
        dfaMin.addToAlphabet(this.alphabet);

        System.out.println("[*] Creating S_min\n");
        for (Set<DFAState> group : groups) {
            dfaMin.addState(DFAState.mergeStates(group));
        }

        System.out.println(dfaMin.getStates());

        System.out.println("[*] Creating δ_min\n");
        dfaMin.lambda = createLambda(dfaMin.getStates());

        System.out.println(dfaMin.lambda);
        System.out.println(dfaMin.listStates);

        System.out.println("[*] Renaming States\n");
        dfaMin.renameStates();

        System.out.println("[*] Done\n");
        return dfaMin;
    }

    private List<Set<DFAState>> buildGroups() {
        List<Set<DFAState>> groups = new ArrayList<>();
        Set<DFAState> endStates = this.getFinalStates();
        Set<DFAState> nonEndStates = new HashSet<>(this.getStates());
        nonEndStates.removeAll(endStates);
        System.out.print("NonEnd states: ");
        System.out.println(nonEndStates);
        groups.add(endStates);
        if (nonEndStates.size() > 0)
            groups.add(nonEndStates);

        boolean groupWasSplit;
        int i = 0;
        do {
            System.out.println("\tΠ" + i++ + " = " + groups);
            List<Set<DFAState>> newPartition = new ArrayList<>();
            groupWasSplit = false;
            for (Set<DFAState> group : groups) {
                if (group.size() == 1) {
                    newPartition.add(group);
                    continue;
                }
                Set<DFAState> oldGroup = new HashSet<>(group);
                Set<DFAState> newGroup = new HashSet<>();

                boolean groupNeedsToBeSplit = false;
                for (String c : this.alphabet) {
                    Set<Set<DFAState>> allGoals = new HashSet<>();

                    for (DFAState s : group) {
                        Set<DFAState> goal = getGroupWhichIncludes(s, c, groups);
                        assert (goal != null);
                        if (allGoals.size() == 0) {
                            allGoals.add(goal);
                        } else if (!allGoals.contains(goal)) {
                            if (!groupNeedsToBeSplit)
                                System.out.println("\n\t\tSplitting " + group);
                            groupNeedsToBeSplit = true;

                            newGroup.add(s);
                            oldGroup.remove(s);
                        }
                    }
                }

                if (groupNeedsToBeSplit) {
                    System.out.println(" into " + oldGroup + " and " + newGroup + "\n");
                    groupWasSplit = true;
                    newPartition.add(newGroup);
                }
                newPartition.add(oldGroup);
            }
            System.out.println('\n');
            groups = newPartition;

        } while (groupWasSplit);
        return groups;
    }

    protected Set<DFAState> getFinalStates() {
        Set<DFAState> finalState = new HashSet<>();
        for (DFAState s : listStates) {
            if (s.isFinal())
                finalState.add(s);
        }
        return finalState;
    }

    private Set<DFAState> getGroupWhichIncludes(DFAState state, String  sym, List<Set<DFAState>> groups) {
        Set<DFAState> result = null;
        state = lambda.getGoalFromTransition(state, sym);
        for (Set<DFAState> group : groups) {
            if (group.contains(state)) {
                result = group;
                break;
            }
        }
        return result;
    }

    private TransitionTable createLambda(List<DFAState> states) {
        TransitionTable result = new TransitionTable();
        for (int i = 0; i < states.size(); ++i) {
            DFAState s = states.get(i);
            DFAState start = s.getIncludedStates().iterator().next();
            for (String c : this.alphabet) {
                DFAState goal = findStateWithIncludedState(lambda.getGoalFromTransition(start, c), new HashSet<>(states));
                if (goal != null) {
                    result.addTransition(new Transition(s, c, goal.asSet()));
                    s.addTransition(goal, c);
                }
            }
            System.out.println(s.getTransitions());
        }
        return result;
    }

    private DFAState findStateWithIncludedState(DFAState s, Set<DFAState> states) {
        DFAState result = null;
        for (DFAState curr : states) {
            if (curr.getIncludedStates().contains(s)) {
                result = curr;
                break;
            }
        }
        return result;
    }

    public Map<String, Set<DFAState>> getPossibleTransitions(DFAState state) {
        return lambda.getPossibleTransitions(state);
    }

    public void renameStates() {
        for (DFAState s : listStates)
            s.setMarked(false);

        String fmtString = "q%d";
        if (listStates.size() >= 100) {
            fmtString = "q%03d";
        } else if (listStates.size() >= 10) {
            fmtString = "q%02d";
        }

        Queue<DFAState> queue = new PriorityQueue<>(listStates.size(), Comparator.comparingLong(DFAState::getId));
        queue.add(this.getStartState());
        int cnt = 1;
        while (!queue.isEmpty()) {
            DFAState state = queue.remove();
            if (state.isMarked())
                continue;
            state.setMarked(true);
            state.setNickname(String.format(fmtString, cnt++));

            for (Set<DFAState> goals : this.getPossibleTransitions(state).values())
                queue.addAll(goals);
        }
    }

    protected DFAState getStartState() {
        DFAState result = null;
        for (DFAState s : listStates) {
            if (s.isStart()) {
                result = s;
                break;
            }
        }
        return result;
    }
}
