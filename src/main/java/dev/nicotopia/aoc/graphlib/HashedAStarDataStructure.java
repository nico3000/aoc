package dev.nicotopia.aoc.graphlib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HashedAStarDataStructure<NodeType> implements AStarDataStructure<NodeType> {
    private final Map<NodeType, Long> fScores = new HashMap<>();
    private final Map<NodeType, Long> gScores = new HashMap<>();
    private final Function<NodeType, Long> estimator;
    private final Predicate<NodeType> isFinal;

    public HashedAStarDataStructure(Function<NodeType, Long> estimator, Predicate<NodeType> isFinal) {
        this.estimator = estimator;
        this.isFinal = isFinal;
    }

    @Override
    public void reset() {
        this.fScores.clear();
        this.gScores.clear();
    }

    @Override
    public void setFScore(NodeType node, long score) {
        this.fScores.put(node, score);
    }

    @Override
    public long getFScore(NodeType node) {
        return Optional.ofNullable(this.fScores.get(node)).orElseGet(() -> this.estimate(node));
    }

    @Override
    public void setGScore(NodeType node, long score) {
        this.gScores.put(node, score);

    }

    @Override
    public long getGScore(NodeType node) {
        return this.gScores.getOrDefault(node, Long.MAX_VALUE);
    }

    @Override
    public long estimate(NodeType node) {
        return this.estimator.apply(node);
    }

    @Override
    public boolean isFinal(NodeType node) {
        return this.isFinal.test(node);
    }

    public Map<NodeType, Long> getFScores() {
        return Collections.unmodifiableMap(this.fScores);
    }
}
