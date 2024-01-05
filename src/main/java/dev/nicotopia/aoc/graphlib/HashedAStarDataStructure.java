package dev.nicotopia.aoc.graphlib;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class HashedAStarDataStructure<NodeType> implements AStarDataStructure<NodeType> {
    private final Map<NodeType, Integer> fScores = new HashMap<>();
    private final Map<NodeType, Integer> gScores = new HashMap<>();
    private final Function<NodeType, Integer> estimator;
    private final Function<NodeType, Boolean> isFinal;

    public HashedAStarDataStructure(Function<NodeType, Integer> estimator, Function<NodeType, Boolean> isFinal) {
        this.estimator = estimator;
        this.isFinal = isFinal;
    }

    @Override
    public void reset() {
        this.fScores.clear();
        this.gScores.clear();
    }

    @Override
    public void setFScore(NodeType node, int score) {
        this.fScores.put(node, score);
    }

    @Override
    public int getFScore(NodeType node) {
        return Optional.ofNullable(this.fScores.get(node)).orElseGet(() -> this.estimate(node));
    }

    @Override
    public void setGScore(NodeType node, int score) {
        this.gScores.put(node, score);

    }

    @Override
    public int getGScore(NodeType node) {
        return this.gScores.getOrDefault(node, Integer.MAX_VALUE);
    }

    @Override
    public int estimate(NodeType node) {
        return this.estimator.apply(node);
    }

    @Override
    public boolean isFinal(NodeType node) {
        return this.isFinal.apply(node);
    }
}
