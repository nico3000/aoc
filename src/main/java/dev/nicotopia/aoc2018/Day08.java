package dev.nicotopia.aoc2018;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import dev.nicotopia.aoc.DayBase;

public class Day08 extends DayBase {
    private class Node {
        private int[] metaData;
        private final List<Node> children = new LinkedList<>();

        public int queryMetaDataSum() {
            return Arrays.stream(this.metaData).sum() + this.children.stream().mapToInt(Node::queryMetaDataSum).sum();
        }

        public int queryValue() {
            return this.children.isEmpty() ? Arrays.stream(this.metaData).sum()
                    : Arrays.stream(this.metaData).filter(i -> i != 0 && i <= this.children.size())
                            .map(i -> this.children.get(i - 1).queryValue()).sum();
        }
    }

    private class IntegerInputStream {
        private final int data[];
        private int ptr = 0;

        public IntegerInputStream(int data[]) {
            this.data = data;
        }

        public int read() {
            return this.data[this.ptr++];
        }

        public int[] read(int count) {
            int start = this.ptr;
            this.ptr += count;
            return Arrays.copyOfRange(this.data, start, this.ptr);
        }
    }

    private Node root;

    private Node parseNode(IntegerInputStream stream) {
        Node n = new Node();
        int childCount = stream.read();
        int metaDataCount = stream.read();
        for (int i = 0; i < childCount; ++i) {
            n.children.add(this.parseNode(stream));
        }
        n.metaData = stream.read(metaDataCount);
        return n;
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2018/day08e.txt");
        this.addTask("Input processing", () -> {
            this.root = this.parseNode(
                    new IntegerInputStream(Arrays.stream(this.getPrimaryPuzzleInput().getFirst().split("\\s+"))
                            .mapToInt(Integer::valueOf).toArray()));
        });
        this.addTask("Part one", () -> this.root.queryMetaDataSum());
        this.addTask("Part two", () -> this.root.queryValue());
    }
}
