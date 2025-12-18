package dev.nicotopia.aoc2024;

import java.util.HashMap;
import java.util.Map;

import dev.nicotopia.Compass4;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.AStarDataStructure;
import dev.nicotopia.aoc.graphlib.BasicGraph;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day21 extends DayBase {
    private class Keypad {
        public enum Type {
            eDirectional, eNumerical
        }

        private final Type type;
        private final Keypad parent;
        private final Map<Vec2i, Map<Vec2i, Long>> minCosts = new HashMap<>();

        public Keypad(Type type, Keypad parent) {
            this.type = type;
            this.parent = parent;
        }

        private boolean isValidPosition(Vec2i pos) {
            int maxY = switch (this.type) {
                case eDirectional -> 1;
                case eNumerical -> 3;
            };
            Vec2i emptyTile = switch (this.type) {
                case eDirectional -> new Vec2i(0, 0);
                case eNumerical -> new Vec2i(0, 3);
            };
            return 0 <= pos.x() && pos.x() < 3 && 0 <= pos.y() && pos.y() <= maxY && !pos.equals(emptyTile);
        }

        public long getMinCosts(Compass4 from, Compass4 to, Compass4 parentPos) {
            return this.getMinCosts(Day21.this.getDirectionalPosition(from), Day21.this.getDirectionalPosition(to),
                    parentPos);
        }

        public long getMinCosts(Vec2i from, Vec2i to, Compass4 parentPos) {
            if (this.parent == null) {
                return 1;
            }
            if (from.equals(to)) {
                return parentPos == null ? 1 : this.parent.getMinCosts(parentPos, null, null);
            }
            Long cost = this.minCosts.getOrDefault(from, new HashMap<>()).get(to);
            if (cost != null) {
                return cost;
            } else if (!this.minCosts.containsKey(from)) {
                this.minCosts.put(from, new HashMap<>());
            }
            AStarDataStructure<Status> asds = new HashedAStarDataStructure<>(
                    s -> (long) s.pos.manhattanDistanceTo(to) + (s.parentLabel == null ? 0 : 1),
                    s -> s.pos.equals(to) && s.parentLabel == null);
            BasicGraph<Status> graph = new BasicGraph<>() {
                @Override
                public NodeDistancePair<Status> getNeighbour(Status current, int index) {
                    if (current.pos.equals(to)) {
                        return index == 0 ? new NodeDistancePair<>(new Status(to, null),
                                Keypad.this.getMinCosts(to, to, current.parentLabel)) : null;
                    } else {
                        for (Compass4 dir : Compass4.values()) {
                            Vec2i nextPos = current.pos.getNeighbour(dir);
                            if (Keypad.this.isValidPosition(nextPos) && index-- == 0) {
                                return new NodeDistancePair<>(new Status(nextPos, dir),
                                        Keypad.this.parent.getMinCosts(current.parentLabel, dir, null));
                            }
                        }
                        return null;
                    }
                }
            };
            cost = AStar.run(graph, new Status(from, null), asds).distance();
            this.minCosts.get(from).put(to, cost);
            return cost;
        }

        private record Status(Vec2i pos, Compass4 parentLabel) {
        }
    }

    private Vec2i getDirectionalPosition(Compass4 dir) {
        return switch (dir) {
            case W -> new Vec2i(0, 1);
            case N -> new Vec2i(1, 0);
            case E -> new Vec2i(2, 1);
            case S -> new Vec2i(1, 1);
            case null -> new Vec2i(2, 0);
        };
    }

    private Vec2i getNumericalPosition(char c) {
        if (c == 'A') {
            return new Vec2i(2, 3);
        } else if (c == '0') {
            return new Vec2i(1, 3);
        } else {
            return new Vec2i((c - '1') % 3, 2 - (c - '1') / 3);
        }
    }

    private long run(int levelCount) {
        Keypad keypad = null;
        for (int i = 0; i < levelCount; ++i) {
            keypad = new Keypad(Keypad.Type.eDirectional, keypad);
        }
        keypad = new Keypad(Keypad.Type.eNumerical, keypad);

        long result = 0;
        for (String code : this.getPrimaryPuzzleInput()) {
            long pressCount = 0;
            Vec2i current = this.getNumericalPosition('A');
            for (char c : code.toCharArray()) {
                Vec2i next = this.getNumericalPosition(c);
                pressCount += keypad.getMinCosts(current, next, null);
                current = next;
            }
            result += Long.parseLong(code.substring(0, 3)) * pressCount;
        }
        return result;
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.run(3));
        this.addTask("Part two", () -> this.run(26));
    }
}
