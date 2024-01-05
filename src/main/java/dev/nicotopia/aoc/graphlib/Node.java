package dev.nicotopia.aoc.graphlib;

public interface Node {
    public NodeDistancePair<Node> getNeighbour(int idx);
}
