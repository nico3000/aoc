package dev.nicotopia;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class GraphUtil {
    public record NodeDistancePair<Node>(Node node, int distance) {
    }

    public interface DisjkstraInterface<Node> {
        /**
         * Returns the distance that is currently applied to the given node.
         * 
         * @param node The node
         * @return The distance
         */
        public Integer getDistance(Node node);

        /**
         * Applies a tentative distance to a given node. When the algorithm finishes the
         * distance is the minimum distance from the start node to the given one.
         * 
         * @param node     The node The node
         * @param distance The distance The new distance
         */
        public void setDistance(Node node, int distance);

        /**
         * Apply an infinite distance to all nodes.
         */
        public void reset();

        /**
         * Retrieves the nth neighbour of a given node together with its distance or
         * null if it does not exist. Once this method returns null for a given idx it
         * is expected to return null for every higher idx as well.
         * 
         * @param node  The base node
         * @param index The neighbour's index
         * @return The neighbour and the distance to it
         */
        public NodeDistancePair<Node> getNeighbour(Node node, int index);
    }

    public static class HashedDijkstraInterface<Node> implements DisjkstraInterface<Node> {
        private final Map<Node, Integer> distances = new HashMap<>();
        private final BiFunction<Node, Integer, NodeDistancePair<Node>> neighbourGetter;

        /**
         * Constructor
         * 
         * @param neighbourGetter {@link DisjkstraInterface#getNeighbour(Object, int)}
         */
        public HashedDijkstraInterface(BiFunction<Node, Integer, NodeDistancePair<Node>> neighbourGetter) {
            this.neighbourGetter = neighbourGetter;
        }

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

        @Override
        public NodeDistancePair<Node> getNeighbour(Node node, int index) {
            return this.neighbourGetter.apply(node, index);
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

    /**
     * Executes the dijkstra algorithm on a given interface.
     * 
     * @param <Node>            The type of the abstract graph's nodes.
     * @param dijkstraInterface The interface
     * @param start             The start node from which the minimum distances to
     *                          all other nodes will be calculated.
     */
    public static <Node> void dijkstra(DisjkstraInterface<Node> dijkstraInterface, Node start) {
        GraphUtil.dijkstra(dijkstraInterface::getDistance, dijkstraInterface::setDistance, dijkstraInterface::reset,
                dijkstraInterface::getNeighbour, start);
    }

    /**
     * Executes the dijkstra algorithm on a given interface.
     * 
     * @param <Node>          The type of the abstract graph's nodes.
     * @param distanceGetter  {@link DisjkstraInterface#getDistance(Object)}
     * @param distanceSetter  {@link DisjkstraInterface#setDistance(Object, int)}
     * @param resetter        {@link DisjkstraInterface#reset()}
     * @param neighbourGetter {@link DisjkstraInterface#getNeighbour(Object, int)}
     * @param start           The start node from which the minimum distances to all
     *                        other nodes will be calculated.
     */
    public static <Node> void dijkstra(Function<Node, Integer> distanceGetter, BiConsumer<Node, Integer> distanceSetter,
            Runnable resetter, BiFunction<Node, Integer, NodeDistancePair<Node>> neighbourGetter, Node start) {
        resetter.run();
        PriorityQueue<Node> visited = new PriorityQueue<>(
                (l, r) -> Integer.compare(distanceGetter.apply(l), distanceGetter.apply(r)));
        distanceSetter.accept(start, 0);
        visited.offer(start);
        while (!visited.isEmpty()) {
            Node c = visited.poll();
            int d = distanceGetter.apply(c);
            int idx = 0;
            NodeDistancePair<Node> neighbour;
            while ((neighbour = neighbourGetter.apply(c, idx)) != null) {
                Integer oldDistance = distanceGetter.apply(neighbour.node);
                int newDistance = d + neighbour.distance;
                if (oldDistance == null || newDistance < oldDistance) {
                    if (oldDistance != null && oldDistance != Integer.MAX_VALUE) {
                        visited.remove(neighbour.node);
                    }
                    distanceSetter.accept(neighbour.node, newDistance);
                    visited.add(neighbour.node);
                }
                ++idx;
            }
        }
    }

    /**
     * Solves the traveling salesman problem with Dijkstra.
     * 
     * @param weights The edge weight matrix
     * @param start   The start node
     * @param closed  If {@code true} the calculated path ends at the starting node
     * @return The length of the shortest path visiting all nodes
     */
    public static int travelingSalesman(int weights[][], int start, boolean closed) {
        int nodeCount = weights.length;
        int visitedMask = (1 << nodeCount) - 1;
        Function<Integer, Integer> getCurrentNode = state -> state >> nodeCount;
        BiFunction<Integer, Integer, Boolean> hasVisited = (state, node) -> (state & (1 << node)) != 0;
        BiFunction<Integer, Integer, Integer> visit = (state, node) -> ((state | (1 << node)) & visitedMask)
                | (node << nodeCount);
        HashedDijkstraInterface<Integer> dijkstraInterface = new HashedDijkstraInterface<Integer>((state, index) -> {
            int current = getCurrentNode.apply(state);
            for (int i = 0; i < nodeCount; ++i) {
                if (!hasVisited.apply(state, i) && index-- == 0) {
                    return new NodeDistancePair<Integer>(visit.apply(state, i), weights[current][i]);
                }
            }
            return null;
        });
        GraphUtil.dijkstra(dijkstraInterface, start << nodeCount);
        Stream<Integer> finalStates = dijkstraInterface.getDistanceMap().keySet().stream()
                .filter(state -> (state & visitedMask) == visitedMask);
        return finalStates.mapToInt(state -> dijkstraInterface.getDistanceMap().get(state)
                + (closed ? weights[getCurrentNode.apply(state)][start] : 0)).min().getAsInt();
    }
}