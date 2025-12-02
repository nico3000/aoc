package dev.nicotopia.aoc.algebra;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.nicotopia.Compass;
import dev.nicotopia.Compass8;
import dev.nicotopia.Pair;

public record Vec2i(int x, int y) {
    public static Vec2i ORIGIN = new Vec2i(0, 0);

    public static Stream<Vec2i> streamFromRectangle(int beginX, int beginY, int endX, int endY) {
        return IntStream.range(beginX, endX).mapToObj(x -> x)
                .flatMap(x -> IntStream.range(beginY, endY).mapToObj(y -> new Vec2i(x, y)));
    }

    public static <T> Stream<Vec2i> streamCoordinatesFor(T[][] arena) {
        return IntStream.range(0, arena.length).mapToObj(y -> y)
                .flatMap(y -> IntStream.range(0, arena[y].length).mapToObj(x -> new Vec2i(x, y)));
    }

    public static <T> Stream<Vec2i> streamCoordinatesFor(char[][] arena) {
        return IntStream.range(0, arena.length).mapToObj(y -> y)
                .flatMap(y -> IntStream.range(0, arena[y].length).mapToObj(x -> new Vec2i(x, y)));
    }

    public static Pair<Vec2i, Vec2i> getExtents(Collection<Vec2i> vectors) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Vec2i v : vectors) {
            minX = Math.min(minX, v.x);
            maxX = Math.max(maxX, v.x);
            minY = Math.min(minY, v.y);
            maxY = Math.max(maxY, v.y);
        }
        return new Pair<>(new Vec2i(minX, minY), new Vec2i(maxX, maxY));
    }

    public Vec2i add(Vec2i other) {
        return new Vec2i(this.x + other.x, this.y + other.y);
    }

    public Vec2i sub(Vec2i other) {
        return new Vec2i(this.x - other.x, this.y - other.y);
    }

    public Vec2i mul(int v) {
        return new Vec2i(this.x * v, this.y * v);
    }

    public Vec2d mul(double v) {
        return new Vec2d((double) this.x * v, (double) this.y * v);
    }

    public Vec2i mulInt(double v) {
        return new Vec2i((int) ((double) this.x * v), (int) ((double) this.y * v));
    }

    public Vec2i divide(int divisor) {
        return new Vec2i(this.x / divisor, this.y / divisor);
    }

    public Vec2i mad(int f, Vec2i v) {
        return this.mul(f).add(v);
    }

    public int manhattanDistanceTo(Vec2i to) {
        return Math.abs(to.x - this.x) + Math.abs(to.y - this.y);
    }

    public Stream<Vec2i> manhattanSphere(int r) {
        Vec2i positions[] = new Vec2i[1 + 2 * r * r + 2 * r];
        int i = 0;
        for (int dy = -r; dy <= r; ++dy) {
            for (int dx = -r + Math.abs(dy); dx <= r - Math.abs(dy); ++dx) {
                positions[i++] = new Vec2i(this.x + dx, this.y + dy);
            }
        }
        return Stream.of(positions);
    }

    public Vec2i getNeighbour(Compass dir) {
        return this.getNeighbour(dir, 1);
    }

    public Vec2i getNeighbour(Compass dir, int numSteps) {
        return switch (dir) {
            case N -> new Vec2i(x, y - numSteps);
            case E -> new Vec2i(x + numSteps, y);
            case S -> new Vec2i(x, y + numSteps);
            case W -> new Vec2i(x - numSteps, y);
        };
    }

    public Vec2i getNeighbour(Compass8 dir) {
        return switch (dir) {
            case N -> new Vec2i(x, y - 1);
            case NE -> new Vec2i(x + 1, y - 1);
            case E -> new Vec2i(x + 1, y);
            case SE -> new Vec2i(x + 1, y + 1);
            case S -> new Vec2i(x, y + 1);
            case SW -> new Vec2i(x - 1, y + 1);
            case W -> new Vec2i(x - 1, y);
            case NW -> new Vec2i(x - 1, y - 1);
        };
    }

    public Stream<Vec2i> neighbours4() {
        return Stream.of(Compass.values()).map(this::getNeighbour);
    }

    public Stream<Vec2i> neighbours8() {
        return Stream.of(Compass8.values()).map(this::getNeighbour);
    }

    @Override
    public String toString() {
        return this.x + "," + this.y;
    }
}
