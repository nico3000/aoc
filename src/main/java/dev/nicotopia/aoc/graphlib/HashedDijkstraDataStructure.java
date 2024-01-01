package dev.nicotopia.aoc.graphlib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HashedDijkstraDataStructure<Node> implements DijkstraDataStructure<Node> {
    private final Map<Node, Integer> distances = new HashMap<>();

    @Override
    public Integer getDistance(Node node) {
        return this.distances.get(node);
    }

    @Override
    public void setDistance(Node node, int distance) {
        this.distances.put(node, distance);
    }

    @Override
    public void reset() {
        this.distances.clear();
    }

    /**
     * Retrieves the map which contains the final minimum distances calculated
     * during Dijkstra execution.
     * 
     * @return The shortest distance map
     */
    public Map<Node, Integer> getDistanceMap() {
        return Collections.unmodifiableMap(this.distances);
    }
}
