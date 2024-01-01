package dev.nicotopia.aoc.graphlib;

public interface BasicGraph<Node> {
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
