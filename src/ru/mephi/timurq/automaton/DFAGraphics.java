package ru.mephi.timurq.automaton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DFAGraphics extends JFrame {
    private static final long serialVersionUID = 1L;
    private final ArrayList<DFAState> stateList = new ArrayList<>();
    private final int distances;
    private final Set<String> symbols;

    public DFAGraphics(DFA automaton) {
        symbols = new HashSet<>();
        symbols.addAll(automaton.getAlphabet());
        System.out.println(automaton.getStates());
        stateList.addAll(automaton.getStates());
        int size = stateList.size();
        distances = (1000 / size);
        this.setSize(1366, 768);
        this.setTitle("DFA");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new DrawHandler(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    private class DrawHandler extends JComponent {
        private static final long serialVersionUID = 1L;

        public void paint(Graphics g) {
            Graphics2D canvas2D = (Graphics2D) g;
            int i = 0;
            for (DFAState ds : stateList) {
                Shape circle = new Ellipse2D.Double(10 + (i) * distances, (726 >> 1) - 50, 50, 50);
                int sign;
                StringBuilder str = new StringBuilder();
                int de;
                Set<Integer> tempset = new HashSet<>();
                boolean flag = true;
                for (String sym : symbols) {
                    if (ds.getTransition(sym) == null) continue;
                    int x = ds.getTransition(sym).getId();
                    if (x == i) {
                        if (flag) {
                            Shape arc1 = new Arc2D.Float(35 + (i) * distances, (726 >> 1) - (60 + 25), 50, 65, 180, -270, Arc2D.OPEN);
                            canvas2D.draw(arc1);
                            flag = false;
                        }
                        str.append(" ").append((sym.charAt(0) == '\\') ? sym.charAt(1) : sym);
                    } else {
                        tempset.add(x - i);
                        de = Math.abs(x - i);
                        if (x - i < 0) {
                            sign = 1;
                            Shape arc = new Arc2D.Float(35 + (i) * distances + (x - i) * distances, (726 >> 1) - (100 - (sign) * 25) - (sign) * 20 * de, Math.abs((x - i) * distances), 150 + 40 * de, 0, -180, Arc2D.OPEN);
                            canvas2D.draw(arc);
                        } else if (i - x < 0) {
                            sign = -1;
                            Shape arc = new Arc2D.Float(35 + (i) * distances, (726 >> 1) - (100 - (sign) * 25) + (sign) * 20 * de, Math.abs((x - i) * distances), 150 + 40 * de, 0, 180, Arc2D.OPEN);
                            canvas2D.draw(arc);
                        }
                    }
                }
                for (int diff : tempset) {
                    StringBuilder str2 = new StringBuilder();
                    int x1 = 1;
                    de = Math.abs(diff);
                    if (diff < 0) x1 = -1;
                    for (String sym : symbols) {
                        if (ds.getTransition(sym) == null) continue;
                        if (ds.getTransition(sym).getId() == diff + i) {
                            str2.append(" ").append((sym.charAt(0) == '\\') ? sym.charAt(1) : sym);
                        }
                    }
                    canvas2D.setFont(new Font("TimesRoman", Font.PLAIN, 25));
                    canvas2D.drawString(str2.toString(), 35 + i * distances + (diff * distances / 2), (int) (726 / 2) - 20 - (x1) * (120 + de * 20));
                    if (diff < 0) {
                        canvas2D.drawString("<", 35 + i * distances + (diff * distances / 2), (int) (726 / 2) - 16 - (x1) * (100 + de * 20));
                    } else {
                        canvas2D.drawString(">", 35 + i * distances + (diff * distances / 2), (int) (726 / 2) - 6 - (x1) * (110 + de * 20));
                    }
                    canvas2D.setFont(new Font("TimesRoman", Font.PLAIN, 14));
                }
                canvas2D.setFont(new Font("TimesRoman", Font.PLAIN, 14));
                canvas2D.drawString("Q" + ds.getId(), 25 + (i) * distances, (726 >> 1) - 25);
                canvas2D.setFont(new Font("TimesRoman", Font.PLAIN, 25));
                canvas2D.drawString(str.toString(), 45 + (i) * distances, (726 >> 1) - 60);
                canvas2D.draw(circle);
                System.out.println(ds.isFinal());
                System.out.println(ds.isTrap());
                if (ds.isFinal()) {
                    canvas2D.setColor(Color.BLUE);
                    Shape circle2 = new Ellipse2D.Double(5 + (i) * distances, (726 >> 1) - 55, 60, 60);
                    canvas2D.draw(circle2);
                    canvas2D.setColor(Color.BLACK);
                }
                if (ds.isTrap()) {
                    canvas2D.setColor(Color.RED);
                    Shape circle2 = new Ellipse2D.Double(5 + (i) * distances, (726 >> 1) - 55, 60, 60);
                    canvas2D.draw(circle2);
                    canvas2D.setColor(Color.BLACK);
                }
                i++;
            }
        }
    }
}
