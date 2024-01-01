package dev.nicotopia.aoc.graphlib;

public interface AStarDataStructure<Node> {
    /**
     * Sets the current best guess for the distance of a node to a final node.
     * 
     * @param n The node
     * @param s The score
     */
    void setFScore(Node n, int s);

    /**
     * Retrieves the last value set by
     * {@link AStarDataStructure#setFScore(Object, int)}. If that never happened, the
     * function should return {@link AStarDataStructure#estimate(Object)}.
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
     * Retrives the last value set by {@link AStarDataStructure#setGScore(Object, int)}
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

    /**
    * Apply an infinite distance to all nodes.
    */
    public void reset();
}
