package ru.mephi.timurq.syntaxtree;

import ru.mephi.timurq.lang.OperatorsSet;
import ru.mephi.timurq.regex.RegExp;
import ru.mephi.timurq.lang.SymbolSet;

import java.util.*;

public class SyntaxTree {
    private int leafNodeId = 0;
    private final OperatorsSet opSet = new OperatorsSet();
    private final SymbolSet symbolSet = new SymbolSet();
    private final Stack<Node> nodeStack = new Stack<>();
    private final Stack<Character> opStack = new Stack<>();
    private Node root;
    private final Set<LeafNode> leaves = new HashSet<>();

    public SyntaxTree(RegExp regex) {
        String regexString = regex.toString();
        buildBinaryTree(regexString);
        computeFunctions();
    }

    public SyntaxTree(String regex) {
        RegExp preparedRegex = new RegExp(regex);
        String regexString = preparedRegex.toString();
        buildBinaryTree(regexString);
        computeFunctions();
    }

    private void buildBinaryTree(String regexString) {
        int len = regexString.length();
        for (int i = 0; i < len; i++) {
            if (isSymbol(regexString.charAt(i))) {
                this.pushNode(regexString.charAt(i));
            } else if (opStack.isEmpty() || regexString.charAt(i) == '(') opStack.push(regexString.charAt(i));
            else if (regexString.charAt(i) == ')') {
                while (opStack.peek() != '(') this.operate();
                opStack.pop();
            } else {
                if (this.priority(opStack.peek(), regexString.charAt(i))) this.operate();
                opStack.push(regexString.charAt(i));
            }
        }
        while (!opStack.isEmpty()) operate();
    }

    private void computeFunctions() {
        this.checkNullable(root);
        this.genFirstPos(root);
        this.genLastPos(root);
        this.genFollowPos(root);
    }

    private void checkNullable(Node root) {
        if (root == null) return;
        checkNullable(root.getLeft());
        checkNullable(root.getRight());
        if (root instanceof LeafNode) return;
        if (root.getSymbol().charAt(0) == '|') {
            if (root.getLeft().isNullable() || root.getRight().isNullable()) root.setNullable(true);
        } else if (root.getSymbol().charAt(0) == '&') {
            if (root.getLeft().isNullable() && root.getRight().isNullable()) root.setNullable(true);
        } else if (root.getSymbol().charAt(0) == '*') {
            root.setNullable(true);
        }
    }

    private void genFirstPos(Node root) {
        if (root == null) return;
        genFirstPos(root.getLeft());
        genFirstPos(root.getRight());
        if (root instanceof LeafNode) {
            LeafNode temp = (LeafNode) root;
            root.addToFirstPos(temp.getId());

        } else if (root.getSymbol().charAt(0) == '|') {
            Set<Integer> tempSetLeft = root.getLeft().getFirstPos();
            Set<Integer> tempSetRight = root.getRight().getFirstPos();
            root.addAllToFirstPos(tempSetLeft);
            root.addAllToFirstPos(tempSetRight);

        } else if (root.getSymbol().charAt(0) == '&') {
            Set<Integer> tempSetLeft = root.getLeft().getFirstPos();
            if (root.getLeft().isNullable()) {
                Set<Integer> tempSetRight = root.getRight().getFirstPos();
                root.addAllToFirstPos(tempSetLeft);
                root.addAllToFirstPos(tempSetRight);
            } else {
                root.addAllToFirstPos(tempSetLeft);
            }

        } else if (root.getSymbol().charAt(0) == '*') {
            Set<Integer> tempSetLeft = root.getLeft().getFirstPos();
            root.addAllToFirstPos(tempSetLeft);
        }
    }

    private void genLastPos(Node root) {
        if (root == null) return;
        genLastPos(root.getLeft());
        genLastPos(root.getRight());
        if (root instanceof LeafNode) {
            LeafNode temp = (LeafNode) root;
            root.addToLastPos(temp.getId());
        } else if (root.getSymbol().charAt(0) == '|') {
            Set<Integer> tempSetLeft = root.getLeft().getLastPos();
            Set<Integer> tempSetRight = root.getRight().getLastPos();
            root.addAllToLastPos(tempSetLeft);
            root.addAllToLastPos(tempSetRight);
        } else if (root.getSymbol().charAt(0) == '&') {
            if (root.getRight().isNullable()) {
                Set<Integer> tempSetLeft = root.getLeft().getLastPos();
                Set<Integer> tempSetRight = root.getRight().getLastPos();
                root.addAllToLastPos(tempSetLeft);
                root.addAllToLastPos(tempSetRight);
            } else {
                Set<Integer> tempSetRight = root.getRight().getLastPos();
                root.addAllToLastPos(tempSetRight);
            }
        } else if (root.getSymbol().charAt(0) == '*') {
            Set<Integer> tempSetLeft = root.getLeft().getLastPos();
            root.addAllToLastPos(tempSetLeft);
        }
    }

    private void genFollowPos(Node root) {
        if (root == null || root instanceof LeafNode) return;
        if (root.getSymbol().charAt(0) == '|') {
            genFollowPos(root.getLeft());
            genFollowPos(root.getRight());
        } else {
            genFollowPos(root.getLeft());
            genFollowPos(root.getRight());
            if (root.getSymbol().charAt(0) == '&') {
                for (int i : root.getLeft().getLastPos()) {
                    Set<Integer> temp = root.getRight().getFirstPos();
                    Objects.requireNonNull(this.getLeaf(i)).addAllToFollowPos(temp);
                }
            } else if (root.getSymbol().charAt(0) == '*') {
                for (int i : root.getLastPos()) {
                    Set<Integer> temp = root.getFirstPos();
                    Objects.requireNonNull(this.getLeaf(i)).addAllToFollowPos(temp);
                }
            }
        }
    }

    private boolean isSymbol(char ch) {
        return symbolSet.contains(ch) && !opSet.contains(ch);
    }

    private LeafNode getLeaf(int id) {
        for (LeafNode node : leaves) {
            if (node.getId() == id) return node;
        }
        return null;
    }

    public void printData() {
        System.out.println("All nodes with Firstpos and Lastpos:");
        this.printInOrder(root);
        System.out.println("All leaves with followpos:");
        this.printDataFollowPos(root);
    }

    private boolean priority(char c1, char c2) {
        if (c1 == c2) return true;
        else if (c1 == '*') return true;
        else if (c2 == '*') return false;
        else if (c1 == '&' && c2 == '|') return true;
        else if (c1 == '(') return false;
        else return false;
    }

    private void operate() {
        char sw = opStack.pop();
        if (sw == '|') union();
        else if (sw == '&') concat();
        else if (sw == '*') closure();
    }

    private void union() {
        Node right = nodeStack.pop();
        Node left = nodeStack.pop();
        Node newRoot = pushNode('|');
        newRoot.setLeft(left);
        newRoot.setRight(right);
        root = newRoot;
    }

    private void concat() {
        Node right = nodeStack.pop();
        Node left = nodeStack.pop();
        Node newRoot = pushNode('&');
        newRoot.setLeft(left);
        newRoot.setRight(right);
        root = newRoot;
    }

    private void closure() {
        Node left = nodeStack.pop();
        Node newRoot = pushNode('*');
        newRoot.setLeft(left);
        root = newRoot;
    }

    private Node pushNode(char id) {
        if (isSymbol(id)) {
            LeafNode newNode = new LeafNode(Character.toString(id), ++leafNodeId);
            nodeStack.push(newNode);
            leaves.add(newNode);
            return newNode;
        } else {
            Node newNode = new Node(Character.toString(id));
            nodeStack.push(newNode);
            return newNode;
        }
    }

    private void printInOrder(Node node) {
        if(node!=null) {
            printInOrder(node.getLeft());
            System.out.println(node.getSymbol()+":-"+node.getFirstPos()+"(FP);"+node.getLastPos()+"(LP)");
            printInOrder(node.getRight());
        }
    }

    private void printDataFollowPos(Node node) {
        if(node != null) {
            printDataFollowPos(node.getLeft());
            if(node instanceof LeafNode) {
                LeafNode temp=(LeafNode)node;
                System.out.println("("+temp.getSymbol()+","+temp.getId()+"):-"+temp.getFollowPos());
            }
            printDataFollowPos(node.getRight());
        }
    }

    void print(String prefix, Node node, boolean isLeft) {
        if (node != null) {
            System.out.print(prefix);
            System.out.print((isLeft ? "├──" : "└──"));
            System.out.println(node.getSymbol());
            print(prefix + (isLeft ? "│   " : "    "), node.getLeft(), true);
            print(prefix + (isLeft ? "│   " : "    "), node.getRight(), false);
        }
    }

    public void print() {
        print("", root, false);
    }

    public Node getRoot() {
        return root;
    }

    public Set<LeafNode> getLeaves() {
        return leaves;
    }
}
