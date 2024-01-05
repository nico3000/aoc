package dev.nicotopia.aoc.graphlib;

import java.util.PriorityQueue;

public class AStar {
    /**
    * Executes the A* algorithm on a given interface.
    * 
    * @param start      The start node from which the minimum distances to all
    *                   other nodes will be calculated.
    * @param asds The A* data structure
    * @return The nearest Node from the start and its total distance to it
    */
    public static NodeDistancePair<Node> run(Node start, AStarDataStructure<Node> asds) {
        return AStar.run(Node::getNeighbour, start, asds);
    }

    /**
    * Executes the A* algorithm on a given interface.
    * 
    * @param <NodeType> The type of the abstract graph's nodes.
    * @param graph      The graph
    * @param start      The start node from which the minimum distances to all
    *                   other nodes will be calculated.
    * @param asds The A* data structure
    * @return The nearest Node from the start and its total distance to it
    */
    public static <NodeType> NodeDistancePair<NodeType> run(BasicGraph<NodeType> graph, NodeType start,
            AStarDataStructure<NodeType> asds) {
        asds.reset();
        PriorityQueue<NodeType> visited = new PriorityQueue<>(
                (l, r) -> Integer.compare(asds.getFScore(l), asds.getFScore(r)));
        asds.setFScore(start, asds.estimate(start));
        asds.setGScore(start, 0);
        visited.offer(start);
        while (!visited.isEmpty()) {
            NodeType c = visited.poll();
            if (asds.isFinal(c)) {
                return new NodeDistancePair<NodeType>(c, asds.getFScore(c));
            }
            int idx = 0;
            NodeDistancePair<NodeType> neighbour;
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
