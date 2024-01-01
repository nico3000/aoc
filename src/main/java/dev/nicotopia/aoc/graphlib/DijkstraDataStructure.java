package dev.nicotopia.aoc.graphlib;

public interface DijkstraDataStructure<Node> {
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
}
