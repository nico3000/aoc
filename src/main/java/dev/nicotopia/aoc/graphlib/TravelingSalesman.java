package dev.nicotopia.aoc.graphlib;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TravelingSalesman {
    /**
     * Traveling salesman algorithm
     * 
     * @param weights The edge weight matrix. {@code weights[i][j]} denotes the
     *                weight from node {@code i} to {@code j}.
     * @param start   The starting node. If empty the shortest round-trip will be
     *                calculated.
     * @return The length of the shortest path or round-trip visiting all nodes
     *         exactly once
     */
    public static int run(int weights[][], OptionalInt start) {
        int nodeCount = weights.length;
        int visitedMask = (1 << nodeCount) - 1;
        Function<Integer, Integer> getCurrentNode = state -> state >> nodeCount;
        BiFunction<Integer, Integer, Boolean> hasVisited = (state, node) -> (state & (1 << node)) != 0;
        BiFunction<Integer, Integer, Integer> visit = (state, node) -> ((state | (1 << node)) & visitedMask)
                | (node << nodeCount);
        HashedDijkstraDataStructure<Integer> dijkstraInterface = new HashedDijkstraDataStructure<Integer>();
        BasicGraph<Integer> graph = (state, index) -> {
            int current = getCurrentNode.apply(state);
            for (int i = 0; i < nodeCount; ++i) {
                if (!hasVisited.apply(state, i) && weights[current][i] != Integer.MAX_VALUE && index-- == 0) {
                    return new NodeDistancePair<Integer>(visit.apply(state, i), weights[current][i]);
                }
            }
            return null;
        };
        Dijkstra.run(graph, visit.apply(0, start.orElse(0)), dijkstraInterface);
        return dijkstraInterface.getDistanceMap().keySet().stream()
                .filter(state -> (state & visitedMask) == visitedMask
                        && (start.isPresent() || weights[getCurrentNode.apply(state)][0] != Integer.MAX_VALUE))
                .mapToInt(state -> dijkstraInterface.getDistanceMap().get(state)
                        + (start.isEmpty() ? weights[getCurrentNode.apply(state)][0] : 0))
                .min().getAsInt();
    }

    /**
     * Traveling salesman algorithm
     * 
     * @param <Node>         Type of nodes
     * @param nodes          A collection of all nodes. Duplicates will be removed.
     * @param weightProvider Provides the edge's weight from first argument's node
     *                       to the second one. {@code Integer.MAX_VALUE} and
     *                       {@code null} are interpreted as no edge.
     * @param start          The starting node. If empty the shortest round-trip
     *                       will be calculated.
     * @return The length of the shortest path or round-trip visiting all nodes
     *         exactly once
     */
    public static <Node> int run(Collection<Node> nodes, BiFunction<Node, Node, Integer> weightProvider,
            Optional<Node> start) {
        List<Node> linearNodes = nodes.stream().distinct().toList();
        int weights[][] = new int[linearNodes.size()][linearNodes.size()];
        for (int i = 0; i < weights.length; ++i) {
            for (int j = 0; j < weights[i].length; ++j) {
                weights[i][j] = Optional.ofNullable(weightProvider.apply(linearNodes.get(i), linearNodes.get(j)))
                        .orElse(Integer.MAX_VALUE);
            }
        }
        Optional<Integer> startIdx = start.map(linearNodes::indexOf);
        return run(weights, startIdx.isPresent() ? OptionalInt.of(startIdx.get()) : OptionalInt.empty());
    }

    /**
     * Traveling salesman algorithm
     * 
     * @param weights The edge weight matrix. {@code weights[i][j]} denotes the
     *                weight from node {@code i} to {@code j}.
     * @return The length of the shortest round-trip visiting all nodes exactly once
     */
    public static int run(int weights[][]) {
        return run(weights, OptionalInt.empty());
    }

    /**
     * Traveling salesman algorithm
     * 
     * @param <Node>         Type of nodes
     * @param nodes          A collection of all nodes. Duplicates will be removed.
     * @param weightProvider Provides the edge's weight from first argument's node
     *                       to the second one. {@code Integer.MAX_VALUE} and
     *                       {@code null} are interpreted as no edge.
     * @return The length of the shortest round-trip visiting all nodes exactly once
     */
    public static <Node> int run(Collection<Node> nodes, BiFunction<Node, Node, Integer> weightProvider) {
        return run(nodes, weightProvider, Optional.empty());
    }
}