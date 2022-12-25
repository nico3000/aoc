package dev.nicotopia;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GraphUtil {
    public record NodeDistancePair<Node>(Node node, int distance) {
    }

    public interface BasicGraphInterface<Node> {
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

    public interface DisjkstraInterface<Node> extends BasicGraphInterface<Node> {
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
    public static <Node> void dijkstra(DisjkstraInterface<Node> di, Node start) {
        GraphUtil.dijkstra(di::getDistance, di::setDistance, di::reset, di::getNeighbour, start);
    }

    /**
     * Executes the dijkstra algorithm on a given interface.
     * 
     * @param <Node>          The type of the abstract graph's nodes.
     * @param distanceGetter  {@link DisjkstraInterface#getDistance(Object)}
     * @param distanceSetter  {@link DisjkstraInterface#setDistance(Object, int)}
     * @param resetter        {@link BasicGraphInterface#reset()}
     * @param neighbourGetter {@link BasicGraphInterface#getNeighbour(Object, int)}
     * @param start           The start node from which the minimum distances to all
     *                        other nodes will be calculated.
     */
    public static <Node> void dijkstra(Function<Node, Integer> distanceGetter,
            BiConsumer<Node, Integer> distanceSetter, Runnable resetter,
            BiFunction<Node, Integer, NodeDistancePair<Node>> neighbourGetter, Node start) {
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

    public interface AStarInterface<Node> extends BasicGraphInterface<Node> {
        /**
         * Sets the current best guess for the distance of a node to a final node.
         * 
         * @param n The node
         * @param s The score
         */
        void setFScore(Node n, int s);

        /**
         * Retrieves the last value set by
         * {@link AStarInterface#setFScore(Object, int)}. If that never happened, the
         * function should return {@link AStarInterface#estimate(Object)}.
         * 
         * @param n The node
         * @return The score
         */
        int getFScore(Node n);

        /**
         * Sets the cheapest path from a node to a final node that is currently known.
         * 
         * @param n The node
         * @param s The score
         */
        void setGScore(Node n, int s);

        /**
         * Retrives the last value set by {@link AStarInterface#setGScore(Object, int)}
         * or Integer.MAX_VALUE if that never happened.
         * 
         * @param n The node
         * @return The score
         */
        int getGScore(Node n);

        /**
         * Estimates the distance from a node to a final distance.
         * 
         * @param n The node
         * @return Estimation of the node's distance to a final node
         */
        int estimate(Node n);

        /**
         * Checks if a node is final.
         * 
         * @param n The node
         * @return {@code true} if final
         */
        boolean isFinal(Node n);
    }

    public static class HashedAStarInterface<Node> implements AStarInterface<Node> {
        private final Map<Node, Integer> fScores = new HashMap<>();
        private final Map<Node, Integer> gScores = new HashMap<>();
        private final BiFunction<Node, Integer, NodeDistancePair<Node>> neightbourGetter;
        private final Function<Node, Integer> estimater;
        private final Function<Node, Boolean> isFinal;

        public HashedAStarInterface(BiFunction<Node, Integer, NodeDistancePair<Node>> neightbourGetter,
                Function<Node, Integer> estimater, Function<Node, Boolean> isFinal) {
            this.neightbourGetter = neightbourGetter;
            this.estimater = estimater;
            this.isFinal = isFinal;
        }

        @Override
        public void reset() {
            this.fScores.clear();
            this.gScores.clear();
        }

        @Override
        public NodeDistancePair<Node> getNeighbour(Node node, int index) {
            return neightbourGetter.apply(node, index);
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
            return this.estimater.apply(n);
        }

        @Override
        public boolean isFinal(Node n) {
            return this.isFinal.apply(n);
        }

    }

    /**
     * Executes the A* algorithm on a given interface.
     * 
     * @param <Node> The type of the abstract graph's nodes.
     * @param start  The start node from which the minimum distances to all
     *               other nodes will be calculated.
     */
    public static <Node> int aStar(AStarInterface<Node> ai, Node start) {
        ai.reset();
        PriorityQueue<Node> visited = new PriorityQueue<>(
                (l, r) -> Integer.compare(ai.getFScore(l), ai.getFScore(r)));
        ai.setFScore(start, ai.estimate(start));
        ai.setGScore(start, 0);
        visited.offer(start);
        while (!visited.isEmpty()) {
            Node c = visited.poll();
            if (ai.isFinal(c)) {
                return ai.getFScore(c);
            }
            int idx = 0;
            NodeDistancePair<Node> neighbour;
            while ((neighbour = ai.getNeighbour(c, idx)) != null) {
                int tentativeGScore = ai.getGScore(c) + neighbour.distance;
                if (tentativeGScore < ai.getGScore(neighbour.node)) {
                    ai.setGScore(neighbour.node, tentativeGScore);
                    ai.setFScore(neighbour.node, tentativeGScore + ai.estimate(neighbour.node));
                    if (visited.contains(neighbour.node)) {
                        visited.remove(neighbour.node);
                    }
                    visited.add(neighbour.node);
                }
                ++idx;
            }
        }
        return Integer.MAX_VALUE;
    }

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
    public static int travelingSalesman(int weights[][], OptionalInt start) {
        int nodeCount = weights.length;
        int visitedMask = (1 << nodeCount) - 1;
        Function<Integer, Integer> getCurrentNode = state -> state >> nodeCount;
        BiFunction<Integer, Integer, Boolean> hasVisited = (state, node) -> (state & (1 << node)) != 0;
        BiFunction<Integer, Integer, Integer> visit = (state, node) -> ((state | (1 << node)) & visitedMask)
                | (node << nodeCount);
        HashedDijkstraInterface<Integer> dijkstraInterface = new HashedDijkstraInterface<Integer>((state, index) -> {
            int current = getCurrentNode.apply(state);
            for (int i = 0; i < nodeCount; ++i) {
                if (!hasVisited.apply(state, i) && weights[current][i] != Integer.MAX_VALUE && index-- == 0) {
                    return new NodeDistancePair<Integer>(visit.apply(state, i), weights[current][i]);
                }
            }
            return null;
        });
        GraphUtil.dijkstra(dijkstraInterface, visit.apply(0, start.orElse(0)));
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
    public static <Node> int travelingSalesman(Collection<Node> nodes, BiFunction<Node, Node, Integer> weightProvider,
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
        return travelingSalesman(weights, startIdx.isPresent() ? OptionalInt.of(startIdx.get()) : OptionalInt.empty());
    }

    /**
     * Traveling salesman algorithm
     * 
     * @param weights The edge weight matrix. {@code weights[i][j]} denotes the
     *                weight from node {@code i} to {@code j}.
     * @return The length of the shortest round-trip visiting all nodes exactly once
     */
    public static int travelingSalesman(int weights[][]) {
        return travelingSalesman(weights, OptionalInt.empty());
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
    public static <Node> int travelingSalesman(Collection<Node> nodes, BiFunction<Node, Node, Integer> weightProvider) {
        return travelingSalesman(nodes, weightProvider, Optional.empty());
    }
}