package ru.mephi.timurq;

import ru.mephi.timurq.automaton.DFA;
import ru.mephi.timurq.automaton.DFAGraphics;
import ru.mephi.timurq.automaton.DFAState;
import ru.mephi.timurq.lang.BasicOperations;
import ru.mephi.timurq.syntaxtree.SyntaxTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class Application {
    public static void main(String[] args) throws IOException {
        System.out.println("---RegExp to DFA CONVERSION PROGRAM---");
        int choiceA = 0, choiceB = 0;
        String str;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (choiceA != 2) {
            System.out.println(
                    "1.Enter the regular expression in string form:\n" +
                            "2.EXIT");
            choiceA = Integer.parseInt(console.readLine());
            if (choiceA == 1) {
                System.out.println("Enter the regEx in string format:-->");
                str = console.readLine();
                while (choiceB != 9) {
                    System.out.println(
                            "1.Generate Syntax Tree\n" +
                                    "2.Generate DFA transitions table\n" +
                                    "3.Check String\n" +
                                    "4.Generate DFA State Diagram (Graphics)\n" +
                                    "5.Generate minDFA\n" +
                                    "6.Get Complement of this Lang\n" +
                                    "7.Get Minimized Complement\n" +
                                    "8.Get minuzzzz.\n" +
                                    "9.Exit"
                    );
                    choiceB = Integer.parseInt(console.readLine());
                    switch (choiceB) {
                        case 1: {
                            SyntaxTree sTreeObj = new SyntaxTree(str);
                            sTreeObj.printData();
                            System.out.println();
                            sTreeObj.print();
                            break;
                        }
                        case 2: {
                            DFA automaton = new DFA(str);
                            automaton.printTable();
                            break;
                        }
                        case 3: {
                            String str2;
                            System.out.println("Enter the string for checking:");
                            str2 = console.readLine();
                            DFA automaton = new DFA(str);
                            automaton.isValidString(str2);
                            break;
                        }
                        case 4: {
                            DFA automaton = new DFA(str);
                            new DFAGraphics(automaton, "Generated DFA (not minimized) Regex:\"" + str + "\"");
                            break;
                        }
                        case 5: {
                            DFAState.resetIds();
                            DFA automaton = new DFA(str);
                            DFA minDFA = automaton.minimize();
                            new DFAGraphics(minDFA, "Generated DFA (minimized) Regex:\"" + str + "\"");
                            break;
                        }
                        case 6: {
                            DFAState.resetIds();
                            DFA automaton = new DFA(str);
                            DFA dfaComplement = automaton.getComplement(Set.of(
                                    "a", "b", "c", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                            ));
                            new DFAGraphics(dfaComplement, Set.of(
                                    "a", "b", "c", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                            ), "Complement 1 (not minimized) Regex:\"" + str + "\"");
                            break;
                        }
                        case 7: {
                            DFAState.resetIds();
                            DFA automaton = new DFA(str);
                            DFA minDFAComplement = automaton.getComplement(Set.of(
                                    "a", "b", "c", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                            )).minimize();
                            new DFAGraphics(minDFAComplement, Set.of(
                                    "a", "b", "c", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                            ), "Coplement 1 (minimized) Regex:\"" + str + "\"");
                            break;
                        }
                        case 8: {
                            DFAState.resetIds();
                            DFA a1 = new DFA("(a|b|d)*c");
                            new DFAGraphics(a1, "DFA_1 (a1) Regex:\"" + "(a|b)*c" + "\"");
                            DFA a2 = new DFA("baac*");
                            new DFAGraphics(a2, "DFA_2 (a2) Regex:\"" + "baac*" + "\"");
                            DFA c = BasicOperations.minus(a1, a2);
                            new DFAGraphics(c, "DFA_3 (a1\\a2) a1: \"" + "(a|b)*c" + "\" a2: \"" + "baac*\"" + " (minimized)");
                        }
                        case 9: {
                            System.out.println("----BACK TO MAIN MENU----");
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected choice: " + choiceB);
                    }
                }
            }
        }
    }
}
