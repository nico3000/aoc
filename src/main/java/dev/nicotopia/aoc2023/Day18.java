package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.Compass;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day18 extends DayBase {
    private record Edge(int x, int y, Compass dir, int length) {
        public Vec2i getEnd() {
            return switch (this.dir) {
                case N -> new Vec2i(this.x, this.y - this.length);
                case E -> new Vec2i(this.x + this.length, this.y);
                case S -> new Vec2i(this.x, this.y + this.length);
                case W -> new Vec2i(this.x - this.length, this.y);
            };
        }

        public boolean intersectsYScanline(int y) {
            return switch (this.dir) {
                case N -> this.y - this.length <= y && y <= this.y;
                case S -> this.y <= y && y <= this.y + this.length;
                default -> this.y == y;
            };
        }

        public int scanlineCompare(Edge other) {
            if (this.getStartX() != other.getStartX()) {
                return Integer.compare(this.getStartX(), other.getStartX());
            }
            return this.dir == Compass.N || this.dir == Compass.S ? -1 : 1;
        }

        public int getStartX() {
            return this.dir == Compass.W ? this.x - this.length : this.x;
        }

        public boolean isVertical() {
            return this.dir == Compass.N || this.dir == Compass.S;
        }
    }

    private List<Edge> getEdges(List<String> instructions) {
        List<Edge> edges = new ArrayList<>(instructions.size());
        Vec2i p = new Vec2i(0, 0);
        for (String line : instructions) {
            String split[] = line.split("\\s+");
            Edge edge = new Edge(p.x(), p.y(), switch (split[0].charAt(0)) {
                case 'U' -> Compass.N;
                case 'R' -> Compass.E;
                case 'D' -> Compass.S;
                case 'L' -> Compass.W;
                default -> throw new AocException("Illegal direction: %s", line);
            }, Integer.valueOf(split[1]));
            edges.add(edge);
            p = edge.getEnd();
        }
        return edges.stream().filter(e -> e.length != 0).toList();
    }

    private List<String> translateInstructions() {
        return this.getPrimaryPuzzleInput().stream().map(l -> {
            String split[] = l.split("\\s+");
            return String.format("%c %d", switch (split[2].charAt(7)) {
                case '0' -> 'R';
                case '1' -> 'D';
                case '2' -> 'L';
                case '3' -> 'U';
                default -> throw new AocException("Illegal color: %s", l);
            }, Integer.valueOf(split[2].substring(2, 7), 16));
        }).toList();
    }

    private long fill(List<String> instructions) {
        List<Edge> edges = this.getEdges(instructions);
        int minY = edges.stream().mapToInt(Edge::y).min().getAsInt();
        int maxY = edges.stream().mapToInt(Edge::y).max().getAsInt();
        return IntStream.rangeClosed(minY, maxY).parallel().mapToLong(y -> {
            List<Edge> sorted = edges.stream().filter(e -> e.intersectsYScanline(y)).sorted(Edge::scanlineCompare)
                    .toList();
            boolean inside = false;
            long count = 0;
            for (int i = 0; i < sorted.size(); ++i) {
                count += inside ? sorted.get(i).x - sorted.get(i - 1).x : 1;
                if (i == sorted.size() - 1 || sorted.get(i + 1).isVertical()) {
                    inside = !inside;
                } else {
                    count += sorted.get(i + 1).length;
                    inside = sorted.get(i).dir == sorted.get(i + 2).dir ? !inside : inside;
                    i += 2;
                }
            }
            return count;
        }).sum();
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2023/day18e.txt");
        List<String> partTwoInstr = this.addSilentTask("Translate instructions", this::translateInstructions);
        this.addTask("Part one", () -> this.fill(this.getPrimaryPuzzleInput()));
        this.addTask("Part two", () -> this.fill(partTwoInstr));
    }
}