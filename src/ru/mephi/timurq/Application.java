package ru.mephi.timurq;

import ru.mephi.timurq.automaton.DFA;
import ru.mephi.timurq.automaton.DFAGraphics;
import ru.mephi.timurq.syntaxtree.SyntaxTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
                while (choiceB != 6) {
                    System.out.println(
                            "1.Generate Syntax Tree\n" +
                                    "2.Generate DFA transitions table\n" +
                                    "3.Check String\n" +
                                    "4.Generate DFA State Diagram (Graphics)\n" +
                                    "5.Generate minDFA\n" +
                                    "6.Exit"
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
                            new DFAGraphics(automaton);
                            break;
                        }
                        case 5: {
                            DFA automaton = new DFA(str);
                            DFA minDFA = automaton.minimize();
                            new DFAGraphics(minDFA);
                        }
                        case 6: {
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
