package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Axis3D;
import dev.nicotopia.aoc.algebra.Vec3i;

public class Day22 extends DayBase {
    private class Brick {
        private interface BlockPredicate {
            public boolean test(int x, int y, int z);
        }

        private Vec3i p;
        private final Axis3D orientation;
        private final int length;
        private final Set<Brick> supportingBricks = new HashSet<>();
        private final Set<Brick> supportedBricks = new HashSet<>();

        public Brick(int ax, int ay, int az, int bx, int by, int bz) {
            this.p = new Vec3i(Math.min(ax, bx), Math.min(ay, by), Math.min(az, bz));
            this.orientation = ax != bx ? Axis3D.X : ay != by ? Axis3D.Y : Axis3D.Z;
            this.length = 1 + Util.largestOf(Math.abs(ax - bx), Math.abs(ay - by), Math.abs(az - bz)).getAsInt();
        }

        public boolean anyBlockMatch(BlockPredicate pred) {
            for (int i = 0; i < this.length; ++i) {
                if (switch (this.orientation) {
                    case X -> pred.test(this.p.x() + i, this.p.y(), this.p.z());
                    case Y -> pred.test(this.p.x(), this.p.y() + i, this.p.z());
                    case Z -> pred.test(this.p.x(), this.p.y(), this.p.z() + i);
                }) {
                    return true;
                }
            }
            return false;
        }

        public boolean isBelow(Brick other) {
            if (this.orientation == Axis3D.Z) {
                return this.p.z() + this.length <= other.p.z()
                        && (other.orientation == Axis3D.Z ? this.p.x() == other.p.x() && this.p.y() == other.p.y()
                                : other.anyBlockMatch((x, y, z) -> x == this.p.x() && y == this.p.y()));
            } else if (this.p.z() < other.p.z()) {
                if (other.orientation == Axis3D.Z) {
                    return this.anyBlockMatch((x, y, z) -> x == other.p.x() && y == other.p.y());
                }
                return other.anyBlockMatch((x, y, z) -> switch (this.orientation) {
                    case Axis3D.X -> this.p.y() == y && this.p.x() <= x && x < this.p.x() + this.length;
                    case Axis3D.Y -> this.p.x() == x && this.p.y() <= y && y < this.p.y() + this.length;
                    default -> throw new AocException();
                });
            }
            return false;
        }

        public int getMaxZ() {
            return this.orientation == Axis3D.Z ? this.p.z() + this.length - 1 : this.p.z();
        }
    }

    private int[][] brickDefinitions;

    private void processInput() {
        Pattern p = Pattern.compile("(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)");
        this.brickDefinitions = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches)
                .map(m -> IntStream.rangeClosed(1, 6).map(i -> Integer.valueOf(m.group(i))).toArray())
                .toArray(int[][]::new);
    }

    private List<Brick> buildBrickList() {
        return new ArrayList<>(
                Arrays.stream(this.brickDefinitions).map(a -> new Brick(a[0], a[1], a[2], a[3], a[4], a[5])).toList());
    }

    private int executeFall(List<Brick> bricks, boolean first) {
        int numFallen = 0;
        List<Brick> sorted = bricks.stream().sorted((a, b) -> a.p.z() - b.p.z()).toList();
        for (int i = 0; i < sorted.size(); ++i) {
            Brick brick = sorted.get(i);
            List<Brick> fixed = sorted.subList(0, i);
            if (first || brick.supportingBricks.isEmpty()) {
                int z = fixed.stream().filter(b -> b.isBelow(brick)).mapToInt(Brick::getMaxZ).max().orElse(0);
                boolean fell = brick.p.z() != z + 1;
                if (fell) {
                    brick.supportedBricks.forEach(b -> b.supportingBricks.remove(brick));
                    brick.supportedBricks.clear();
                    brick.p = new Vec3i(brick.p.x(), brick.p.y(), z + 1);
                    ++numFallen;
                }
                if (first || fell) {
                    fixed.stream().filter(b -> b.getMaxZ() == z && b.isBelow(brick)).forEach(b -> {
                        brick.supportingBricks.add(b);
                        b.supportedBricks.add(brick);
                    });
                }
            }
        }
        return numFallen;
    }

    private int partOne() {
        List<Brick> bricks = this.buildBrickList();
        this.executeFall(bricks, true);
        return (int) bricks.stream()
                .filter(brick -> brick.supportedBricks.stream().allMatch(b -> b.supportingBricks.size() != 1)).count();
    }

    private int partTwo() {
        return IntStream.range(0, this.getPrimaryPuzzleInput().size()).parallel().map(i -> {
            List<Brick> bricks = this.buildBrickList();
            this.executeFall(bricks, true);
            Brick brick = bricks.remove(i);
            brick.supportingBricks.forEach(b -> b.supportedBricks.remove(brick));
            brick.supportedBricks.forEach(b -> b.supportingBricks.remove(brick));
            return this.executeFall(bricks, false);
        }).sum();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part tow", this::partTwo);
    }
}