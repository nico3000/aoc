package dev.nicotopia.aoc.graphlib;

public interface Node<E extends Node<E>> {
    public NodeDistancePair<E> getNeighbour(int idx);
}
