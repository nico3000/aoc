package dev.nicotopia.aoc2018;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.Pair;
import dev.nicotopia.Util;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec3i;

public class Day23 extends DayBase {
    private record Nanobot(Vec3i pos, int range) {
        public boolean isInRange(int x, int y, int z) {
            return Math.abs(this.pos.x() - x) + Math.abs(this.pos.y() - y) + Math.abs(this.pos.z() - z) <= this.range;
        }
    }

    private record Cube(Vec3i base, int size) {
        public boolean contains(int x, int y, int z) {
            return this.base.x() <= x && x < this.base.x() + this.size && this.base.y() <= y
                    && y < this.base.y() + this.size && this.base.z() <= z && z < this.base.z() + this.size;
        }

        public Cube[] divide() {
            return new Cube[] {
                    new Cube(this.base, this.size / 2),
                    new Cube(new Vec3i(this.base.x() + this.size / 2, this.base.y(), this.base.z()), this.size / 2),
                    new Cube(new Vec3i(this.base.x(), this.base.y() + this.size / 2, this.base.z()), this.size / 2),
                    new Cube(new Vec3i(this.base.x() + this.size / 2, this.base.y() + this.size / 2, this.base.z()),
                            this.size / 2),
                    new Cube(new Vec3i(this.base.x(), this.base.y(), this.base.z() + this.size / 2), this.size / 2),
                    new Cube(new Vec3i(this.base.x() + this.size / 2, this.base.y(), this.base.z() + this.size / 2),
                            this.size / 2),
                    new Cube(new Vec3i(this.base.x(), this.base.y() + this.size / 2, this.base.z() + this.size / 2),
                            this.size / 2),
                    new Cube(new Vec3i(this.base.x() + this.size / 2, this.base.y() + this.size / 2,
                            this.base.z() + this.size / 2), this.size / 2)
            };
        }

        public boolean intersects(Nanobot n) {
            return this.contains(n.pos.x() - n.range, n.pos.y(), n.pos.z())
                    || this.contains(n.pos.x() + n.range, n.pos.y(), n.pos.z())
                    || this.contains(n.pos.x(), n.pos.y() - n.range, n.pos.z())
                    || this.contains(n.pos.x(), n.pos.y() + n.range, n.pos.z())
                    || this.contains(n.pos.x(), n.pos.y(), n.pos.z() - n.range)
                    || this.contains(n.pos.x(), n.pos.y(), n.pos.z() + n.range)
                    || n.isInRange(this.base.x(), this.base.y(), this.base.z())
                    || n.isInRange(this.base.x() + this.size - 1, this.base.y(), this.base.z())
                    || n.isInRange(this.base.x(), this.base.y() + this.size - 1, this.base.z())
                    || n.isInRange(this.base.x() + this.size - 1, this.base.y() + this.size - 1, this.base.z())
                    || n.isInRange(this.base.x(), this.base.y(), this.base.z() + this.size - 1)
                    || n.isInRange(this.base.x() + this.size - 1, this.base.y(), this.base.z() + this.size - 1)
                    || n.isInRange(this.base.x(), this.base.y() + this.size - 1, this.base.z() + this.size - 1)
                    || n.isInRange(this.base.x() + this.size - 1, this.base.y() + this.size - 1,
                            this.base.z() + this.size - 1);
        }
    }

    private List<Nanobot> nanobots;

    public int getNumNanobotsInRange(Cube c) {
        return (int) this.nanobots.stream().filter(c::intersects).count();
    }

    private void processInput() {
        Pattern p = Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)");
        this.nanobots = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches)
                .map(m -> IntStream.range(1, 5).map(i -> Integer.valueOf(m.group(i))).toArray())
                .map(v -> new Nanobot(new Vec3i(v[0], v[1], v[2]), v[3])).toList();
    }

    private int partOne() {
        Nanobot max = this.nanobots.stream().max((a, b) -> Integer.compare(a.range, b.range)).get();
        return (int) this.nanobots.stream().filter(o -> max.pos.manhattanDistance(o.pos) <= max.range).count();
    }

    private long partTwo() {
        Vec3i min = new Vec3i(this.nanobots.stream().mapToInt(n -> n.pos.x() - n.range).min().getAsInt(),
                this.nanobots.stream().mapToInt(n -> n.pos.y() - n.range).min().getAsInt(),
                this.nanobots.stream().mapToInt(n -> n.pos.z() - n.range).min().getAsInt());
        Vec3i max = new Vec3i(this.nanobots.stream().mapToInt(n -> n.pos.x() - n.range).max().getAsInt(),
                this.nanobots.stream().mapToInt(n -> n.pos.y() - n.range).max().getAsInt(),
                this.nanobots.stream().mapToInt(n -> n.pos.z() - n.range).max().getAsInt());
        int initialCubeSize = 2 * Integer.highestOneBit(Util.largestOf(max.x() - min.x(), max.y() - min.y(),
                max.z() - min.z()) + 1);
        PriorityQueue<Pair<Cube, Integer>> queue = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.second(), a.second()));
        queue.offer(new Pair<>(new Cube(min, initialCubeSize), this.nanobots.size()));
        List<Vec3i> foundCoords = new LinkedList<>();
        int foundNum = 0;
        while (!queue.isEmpty()) {
            var p = queue.poll();
            if (!foundCoords.isEmpty() && p.second() < foundNum) {
                break;
            } else if (p.first().size == 1) {
                foundCoords.add(p.first().base());
                foundNum = p.second();
            } else {
                for (Cube c : p.first().divide()) {
                    queue.offer(new Pair<>(c, this.getNumNanobotsInRange(c)));
                }
            }
        }
        return !foundCoords.isEmpty() ? foundCoords.stream()
                .min((a, b) -> Integer.compare(a.manhattanDistance(Vec3i.ORIGIN), b.manhattanDistance(Vec3i.ORIGIN)))
                .map(c -> c.x() + c.y() + c.z()).get() : 0;
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
