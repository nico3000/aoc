package dev.nicotopia.aoc2018;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.Dijkstra;
import dev.nicotopia.aoc.graphlib.HashedDijkstraDataStructure;
import dev.nicotopia.aoc.graphlib.Node;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day20 extends DayBase {
    private final Map<Vec2i, Room> rooms = new HashMap<>();
    private final HashedDijkstraDataStructure<Node> dds = new HashedDijkstraDataStructure<>();
    private final Room start = new Room(0, 0);

    private class Room implements Node {
        private final Vec2i coords;
        private final List<Room> doors = new ArrayList<>(4);

        public Room(int x, int y) {
            this.coords = new Vec2i(x, y);
        }

        public Room addDoor(Vec2i to) {
            Room other = Day20.this.rooms.get(to);
            if (other == null) {
                Day20.this.rooms.put(to, other = new Room(to.x(), to.y()));
            }
            if (!this.doors.contains(other)) {
                this.doors.add(other);
                other.doors.add(this);
            }
            return other;
        }

        @Override
        public NodeDistancePair<Node> getNeighbour(int idx) {
            return this.doors.size() <= idx ? null : new NodeDistancePair<Node>(this.doors.get(idx), 1);
        }
    }

    private void processInput() {
        this.rooms.put(this.start.coords, this.start);
        Room current = this.start;
        Stack<Room> stack = new Stack<>();
        for (char c : this.getPrimaryPuzzleInput().getFirst().toCharArray()) {
            switch (c) {
                case 'N' -> current = current.addDoor(new Vec2i(current.coords.x(), current.coords.y() - 1));
                case 'E' -> current = current.addDoor(new Vec2i(current.coords.x() + 1, current.coords.y()));
                case 'S' -> current = current.addDoor(new Vec2i(current.coords.x(), current.coords.y() + 1));
                case 'W' -> current = current.addDoor(new Vec2i(current.coords.x() - 1, current.coords.y()));
                case '|' -> current = stack.peek();
                case '(' -> stack.push(current);
                case ')' -> current = stack.pop();
            }
        }
    }

    private int partOne() {
        return this.dds.getDistanceMap().values().stream().mapToInt(i -> i).max().getAsInt();
    }

    private long partTwo() {
        return this.dds.getDistanceMap().values().stream().mapToInt(i -> i).filter(i -> 1000 <= i).count();
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example 1", "/2018/day20e1.txt");
        this.addPresetFromResource("Example 2", "/2018/day20e2.txt");
        this.addPresetFromResource("Example 3", "/2018/day20e3.txt");
        this.addPresetFromResource("Example 4", "/2018/day20e4.txt");
        this.addPresetFromResource("Example 5", "/2018/day20e5.txt");
        this.addTask("Process input", this::processInput);
        this.addTask("Dijkstra", () -> Dijkstra.run(this.start, this.dds));
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
