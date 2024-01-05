package dev.nicotopia.aoc.graphlib;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.graphlib.Karger.Result;

public class KargerStein<NodeType> {
    private final Karger<NodeType> karger = new Karger<>();

    public KargerStein(Collection<Pair<NodeType, NodeType>> edges) {
        edges.forEach(this.karger::addEdge);
    }

    public Optional<Result<NodeType>> run() {
        Random r = new Random();
        this.karger.init();
        this.karger.runUntil(r.nextLong(), (int) ((double) this.karger.getNumSuperNodes() / Math.sqrt(2.0)));
        Karger<NodeType> s1 = this.karger.cloneState();
        Karger<NodeType> s2 = this.karger.cloneState();
        var r1 = s1.run(r.nextLong());
        var r2 = s2.run(r.nextLong());
        if (r1.isPresent() && r2.isPresent()) {
            return r1.get().minCut() < r2.get().minCut() ? r1 : r2;
        } else if (r1.isPresent()) {
            return r1;
        } else {
            return r2;
        }
    }
}
