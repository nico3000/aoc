package dev.nicotopia.aoc.graphlib;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class HashedAStarDataStructure<Node> implements AStarDataStructure<Node> {
    private final Map<Node, Integer> fScores = new HashMap<>();
    private final Map<Node, Integer> gScores = new HashMap<>();
    private final Function<Node, Integer> estimator;
    private final Function<Node, Boolean> isFinal;

    public HashedAStarDataStructure(Function<Node, Integer> estimator, Function<Node, Boolean> isFinal) {
        this.estimator = estimator;
        this.isFinal = isFinal;
    }

    @Override
    public void reset() {
        this.fScores.clear();
        this.gScores.clear();
    }

    @Override
    public void setFScore(Node n, int s) {
        this.fScores.put(n, s);
    }

    @Override
    public int getFScore(Node n) {
        return Optional.ofNullable(this.fScores.get(n)).orElseGet(() -> this.estimate(n));
    }

    @Override
    public void setGScore(Node n, int s) {
        this.gScores.put(n, s);

    }

    @Override
    public int getGScore(Node n) {
        return this.gScores.getOrDefault(n, Integer.MAX_VALUE);
    }

    @Override
    public int estimate(Node n) {
        return this.estimator.apply(n);
    }

    @Override
    public boolean isFinal(Node n) {
        return this.isFinal.apply(n);
    }
}
