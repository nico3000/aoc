package dev.nicotopia.aoc.algebra;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.nicotopia.Pair;

public record Vec2i(int x, int y) {
    public static Vec2i ORIGIN = new Vec2i(0, 0);

    public static Stream<Vec2i> streamFromRectangle(int beginX, int beginY, int endX, int endY) {
        return IntStream.range(beginX, endX)
                .mapToObj(x -> IntStream.range(beginY, endY).mapToObj(y -> new Vec2i(x, y)).toList())
                .collect(LinkedList<Vec2i>::new, LinkedList::addAll, LinkedList::addAll).stream();
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

    @Override
    public String toString() {
        return this.x + "," + this.y;
    }
}
