package dev.nicotopia.aoc2025;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec3i;

public class Day08 extends DayBase {
    private class Circuit {
        private final Set<Box> boxes = new HashSet<>();
    }

    private class Box {
        public static final Set<Circuit> knownCircuits = new HashSet<>();

        private final Vec3i pos;
        private final Set<Box> connections = new HashSet<>();
        private Circuit circuit = new Circuit();

        public Box(Vec3i pos) {
            this.pos = pos;
            this.circuit.boxes.add(this);
            Box.knownCircuits.add(this.circuit);
        }

        private void changeCircuit(Circuit newCircuit) {
            if (this.circuit != newCircuit) {
                this.circuit = newCircuit;
                this.circuit.boxes.add(this);
                for (Box box : this.connections) {
                    box.changeCircuit(newCircuit);
                }
            }
        }

        public void connect(Box other) {
            this.connections.add(other);
            if (this.circuit != other.circuit) {
                Box.knownCircuits.remove(other.circuit);
                other.changeCircuit(this.circuit);
            }
            other.connections.add(this);
        }
    }

    private record Connection(Box a, Box b, double distance) {
        public Connection(Box a, Box b) {
            this(a, b, a.pos.distance(b.pos));
        }
    }

    private Pair<Integer, Long> partOneAndTwo(List<Box> boxes, int partOneMergeCount) {
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < boxes.size(); ++i) {
            for (int j = i + 1; j < boxes.size(); ++j) {
                connections.add(new Connection(boxes.get(i), boxes.get(j)));
            }
        }
        Collections.sort(connections, (a, b) -> Double.compare(a.distance, b.distance));
        for (int i = 0; i < partOneMergeCount; ++i) {
            connections.get(i).a.connect(connections.get(i).b);
        }
        List<Circuit> circuits = boxes.stream().map(b -> b.circuit).distinct()
                .sorted((a, b) -> Integer.compare(a.boxes.size(), b.boxes.size())).toList();
        int partOne = circuits.get(circuits.size() - 3).boxes.size() * circuits.get(circuits.size() - 2).boxes.size()
                * circuits.get(circuits.size() - 1).boxes.size();
        for (int i = partOneMergeCount; i < connections.size(); ++i) {
            Connection c = connections.get(i);
            c.a.connect(c.b);
            if (Box.knownCircuits.size() == 1) {
                return new Pair<>(partOne, (long) c.a.pos.x() * (long) c.b.pos.x());
            }
        }
        return null;
    }

    @Override
    public void run() {
        this.pushSecondaryInput("Part one: merge count", 1000);
        this.addPresetFromResource("Example", "/2025/day08e.txt", 10);
        List<Box> boxes = this.getPrimaryPuzzleInput().stream().map(line -> line.split(","))
                .map(s -> new Box(new Vec3i(Integer.valueOf(s[0]), Integer.valueOf(s[1]), Integer.valueOf(s[2]))))
                .toList();
        Pair<Integer, Long> solutions = this.addSilentTask("Algorithm",
                () -> this.partOneAndTwo(boxes, this.getIntInput("Part one: merge count")));
        this.addTask("Part two", () -> solutions.first());
        this.addTask("Part two", () -> solutions.second());
    }
}
