package dev.nicotopia.aoc.graphlib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import dev.nicotopia.Pair;

public class Karger<NodeType> {
    public record Result<NodeType>(int minCut, Set<NodeType> setA, Set<NodeType> setB) {
    }

    private class SuperNode {
        private final Set<NodeType> nodes = new HashSet<>();

        @Override
        public String toString() {
            String str = "";
            int c = 0;
            for (NodeType n : this.nodes) {
                if (6 < ++c) {
                    return String.format("%s|...(%d)", str, this.nodes.size() - c + 1);
                }
                str += (c == 1 ? "" : "|") + n.toString();
            }
            return str;
        }
    }

    private class SuperEdge {
        private final SuperNode snA;
        private final SuperNode snB;
        private final List<Pair<NodeType, NodeType>> edges = new LinkedList<>();

        public SuperEdge(SuperNode a, SuperNode b) {
            this.snA = a;
            this.snB = b;
        }

        @Override
        public String toString() {
            return this.snA + " – " + this.snB;
        }
    }

    private List<Pair<NodeType, NodeType>> originalEdges = new LinkedList<>();
    private List<SuperNode> superNodes = new LinkedList<>();
    private Set<SuperEdge> superEdges = new HashSet<>();
    private List<Pair<NodeType, NodeType>> currentEdges = new LinkedList<>();

    public void addEdge(NodeType a, NodeType b) {
        this.originalEdges.add(new Pair<>(a, b));
    }

    public void addEdge(Pair<NodeType, NodeType> edge) {
        this.originalEdges.add(edge);
    }

    private SuperNode findSuperNode(NodeType n) {
        return this.superNodes.stream().filter(sn -> sn.nodes.contains(n)).findAny().get();
    }

    public int getNumSuperNodes() {
        return this.superNodes.size();
    }

    public void init() {
        this.superNodes.clear();
        this.superEdges.clear();
        this.currentEdges.clear();
        this.currentEdges.addAll(this.originalEdges);
        this.originalEdges.stream().mapMulti((Pair<NodeType, NodeType> t, Consumer<NodeType> u) -> {
            u.accept(t.first());
            u.accept(t.second());
        }).distinct().forEach(n -> {
            SuperNode sn = new SuperNode();
            sn.nodes.add(n);
            this.superNodes.add(sn);
        });
        for (Pair<NodeType, NodeType> edge : this.originalEdges) {
            SuperEdge se = new SuperEdge(
                    this.superNodes.stream().filter(sn -> sn.nodes.contains(edge.first())).findAny().get(),
                    this.superNodes.stream().filter(sn -> sn.nodes.contains(edge.second())).findAny().get());
            se.edges.add(edge);
            this.superEdges.add(se);
        }
    }

    private void merge(SuperNode a, SuperNode b) {
        SuperNode newSuperNode = new SuperNode();
        newSuperNode.nodes.addAll(a.nodes);
        newSuperNode.nodes.addAll(b.nodes);
        this.superNodes.remove(a);
        this.superNodes.remove(b);
        var edges = this.superEdges.stream().filter(se -> se.snA == a || se.snA == b || se.snB == a || se.snB == b)
                .collect(Collectors.groupingBy(t -> t.snA == a || t.snA == b ? t.snB : t.snA));
        for (var e : edges.entrySet()) {
            this.superEdges.removeAll(e.getValue());
            if (e.getKey() != a && e.getKey() != b) {
                SuperEdge newSuperEdge = new SuperEdge(newSuperNode, e.getKey());
                e.getValue().forEach(se -> newSuperEdge.edges.addAll(se.edges));
                this.superEdges.add(newSuperEdge);
            }
        }
        this.superNodes.add(newSuperNode);
    }

    public void runUntil(long seed, int remSuperNodes) {
        Random r = new Random(seed);
        while (remSuperNodes < this.superNodes.size() && !this.currentEdges.isEmpty()) {
            Pair<NodeType, NodeType> edge = this.currentEdges.remove(r.nextInt(this.currentEdges.size()));
            SuperNode a = this.findSuperNode(edge.first());
            SuperNode b = this.findSuperNode(edge.second());
            this.merge(a, b);
        }
    }

    public Optional<Result<NodeType>> run(long seed) {
        this.runUntil(seed, 2);
        if (this.superEdges.size() != 1 || this.superNodes.size() != 2) {
            return Optional.empty();
        }
        return Optional.of(new Result<>(this.superEdges.iterator().next().edges.size(),
                this.superNodes.getFirst().nodes, this.superNodes.getLast().nodes));
    }

    public Karger<NodeType> cloneState() {
        Karger<NodeType> clone = new Karger<>();
        clone.originalEdges.addAll(this.originalEdges);
        clone.currentEdges.addAll(this.currentEdges);
        Map<SuperNode, SuperNode> clonedSuperNodes = new HashMap<>();
        for (SuperNode sn : this.superNodes) {
            SuperNode c = new SuperNode();
            c.nodes.addAll(sn.nodes);
            clone.superNodes.add(c);
            clonedSuperNodes.put(sn, c);
        }
        for (SuperEdge se : this.superEdges) {
            SuperEdge c = new SuperEdge(clonedSuperNodes.get(se.snA), clonedSuperNodes.get(se.snB));
            c.edges.addAll(se.edges);
            clone.superEdges.add(c);
        }
        return clone;
    }
}