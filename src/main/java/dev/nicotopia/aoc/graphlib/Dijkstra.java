package dev.nicotopia.aoc.graphlib;

import java.util.PriorityQueue;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Dijkstra {
    /**
    * Executes the dijkstra algorithm on a given interface.
    * 
    * @param <NodeType> The type of the abstract graph's nodes.
    * @param graph      The graph
    * @param start      The start node from which the minimum distances to all other nodes will be calculated.
    * @param dds        The Disjkstra data structure
    */
    public static <NodeType> void run(BasicGraph<NodeType> graph, NodeType start, DijkstraDataStructure<NodeType> dds) {
        Dijkstra.run(dds::getDistance, dds::setDistance, dds::reset, graph::getNeighbour, start);
    }

    /**
    * Executes the dijkstra algorithm on a given interface.
    * 
    * @param start The start node from which the minimum distances to all other nodes will be calculated.
    * @param dds   The Disjkstra data structure
    */
    public static void run(Node start, DijkstraDataStructure<Node> dds) {
        Dijkstra.run(dds::getDistance, dds::setDistance, dds::reset, Node::getNeighbour, start);
    }

    /**
     * Executes the dijkstra algorithm on a given interface.
     * 
     * @param <NodeType>      The type of the abstract graph's nodes.
     * @param distanceGetter  {@link DisjkstraInterface#getDistance(Object)}
     * @param distanceSetter  {@link DisjkstraInterface#setDistance(Object, int)}
     * @param resetter        {@link BasicGraphInterface#reset()}
     * @param neighbourGetter {@link BasicGraphInterface#getNeighbour(Object, int)}
     * @param start           The start node from which the minimum distances to all
     *                        other nodes will be calculated.
     */
    public static <NodeType> void run(Function<NodeType, Integer> distanceGetter,
            BiConsumer<NodeType, Integer> distanceSetter, Runnable resetter,
            BiFunction<NodeType, Integer, NodeDistancePair<NodeType>> neighbourGetter, NodeType start) {
        resetter.run();
        PriorityQueue<NodeType> visited = new PriorityQueue<>(
                (l, r) -> Integer.compare(distanceGetter.apply(l), distanceGetter.apply(r)));
        distanceSetter.accept(start, 0);
        visited.offer(start);
        while (!visited.isEmpty()) {
            NodeType c = visited.poll();
            int d = distanceGetter.apply(c);
            int idx = 0;
            NodeDistancePair<NodeType> neighbour;
            while ((neighbour = neighbourGetter.apply(c, idx)) != null) {
                Integer oldDistance = distanceGetter.apply(neighbour.node());
                int newDistance = d + neighbour.distance();
                if (oldDistance == null || newDistance < oldDistance) {
                    if (oldDistance != null && oldDistance != Integer.MAX_VALUE) {
                        visited.remove(neighbour.node());
                    }
                    distanceSetter.accept(neighbour.node(), newDistance);
                    visited.add(neighbour.node());
                }
                ++idx;
            }
        }
    }
}
