package ru.mephi.timurq.syntaxtree;

import java.util.HashSet;
import java.util.Set;

public class LeafNode extends Node {
    private final int id;
    private final Set<Integer> followPos;

    public LeafNode(String symbol, int id) {
        super(symbol);
        this.id = id;
        followPos = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public Set<Integer> getFollowPos() {
        return followPos;
    }

    public void addAllToFollowPos(Set<Integer> followPos) {
        this.followPos.addAll(followPos);
    }
}
