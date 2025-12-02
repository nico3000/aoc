package dev.nicotopia.aoc.graphlib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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

    public Stream<NodeDistancePair<NodeType>> nodeDistancePairs(boolean parallel) {
        var stream = this.distances.entrySet().stream();
        return (parallel ? stream.parallel() : stream).map(e -> new NodeDistancePair<>(e.getKey(), e.getValue()));
    }
}
