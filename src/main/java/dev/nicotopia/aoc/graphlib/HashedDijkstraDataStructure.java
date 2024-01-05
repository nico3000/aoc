package dev.nicotopia.aoc.graphlib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HashedDijkstraDataStructure<NodeType> implements DijkstraDataStructure<NodeType> {
    private final Map<NodeType, Integer> distances = new HashMap<>();

    @Override
    public Integer getDistance(NodeType node) {
        return this.distances.get(node);
    }

    @Override
    public void setDistance(NodeType node, int distance) {
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
    public Map<NodeType, Integer> getDistanceMap() {
        return Collections.unmodifiableMap(this.distances);
    }
}
