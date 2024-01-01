package dev.nicotopia.aoc.graphlib;

import java.util.PriorityQueue;

public class AStar {
    /**
    * Executes the A* algorithm on a given interface.
    * 
    * @param <Node> The type of the abstract graph's nodes.
    * @param graph  The graph
    * @param start  The start node from which the minimum distances to all
    *               other nodes will be calculated.
    * @param asds   The A* data structure
    */
    public static <Node> NodeDistancePair<Node> run(BasicGraph<Node> graph, Node start,
            AStarDataStructure<Node> asds) {
        asds.reset();
        PriorityQueue<Node> visited = new PriorityQueue<>(
                (l, r) -> Integer.compare(asds.getFScore(l), asds.getFScore(r)));
        asds.setFScore(start, asds.estimate(start));
        asds.setGScore(start, 0);
        visited.offer(start);
        while (!visited.isEmpty()) {
            Node c = visited.poll();
            if (asds.isFinal(c)) {
                return new NodeDistancePair<Node>(c, asds.getFScore(c));
            }
            int idx = 0;
            NodeDistancePair<Node> neighbour;
            while ((neighbour = graph.getNeighbour(c, idx)) != null) {
                int tentativeGScore = asds.getGScore(c) + neighbour.distance();
                if (tentativeGScore < asds.getGScore(neighbour.node())) {
                    asds.setGScore(neighbour.node(), tentativeGScore);
                    asds.setFScore(neighbour.node(), tentativeGScore + asds.estimate(neighbour.node()));
                    if (visited.contains(neighbour.node())) {
                        visited.remove(neighbour.node());
                    }
                    visited.add(neighbour.node());
                }
                ++idx;
            }
        }
        return null;
    }
}
