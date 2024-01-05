package dev.nicotopia.aoc.graphlib;

public interface AStarDataStructure<NodeType> {
    /**
     * Sets the current best guess for the distance of a node to a final node.
     * 
     * @param node  The node
     * @param score The new f score
     */
    void setFScore(NodeType node, int score);

    /**
     * Retrieves the last value set by
     * {@link AStarDataStructure#setFScore(Object, int)}. If that never happened, the
     * function should return {@link AStarDataStructure#estimate(Object)}.
     * 
     * @param node The node
     * @return The current f score
     */
    int getFScore(NodeType node);

    /**
     * Sets the cheapest path from a node to a final node that is currently known.
     * 
     * @param node  The node
     * @param score The new g score
     */
    void setGScore(NodeType node, int score);

    /**
     * Retrives the last value set by {@link AStarDataStructure#setGScore(Object, int)}
     * or Integer.MAX_VALUE if that never happened.
     * 
     * @param node The node
     * @return The current g score
     */
    int getGScore(NodeType node);

    /**
     * Estimates the distance from a node to a final distance.
     * 
     * @param node The node
     * @return Estimation of the node's distance to a final node
     */
    int estimate(NodeType node);

    /**
     * Checks if a node is final.
     * 
     * @param node The node
     * @return {@code true} if final
     */
    boolean isFinal(NodeType node);

    /**
    * Apply an infinite distance to all nodes.
    */
    public void reset();
}
