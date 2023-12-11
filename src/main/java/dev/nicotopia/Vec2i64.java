package dev.nicotopia;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public record Vec2i64(long x, long y) {
    public static Stream<Vec2i64> streamFromRectangle(long beginX, long beginY, long endX, long endY) {
        return LongStream.range(beginX, endX)
                .mapToObj(x -> LongStream.range(beginY, endY).mapToObj(y -> new Vec2i64(x, y)).toList())
                .collect(LinkedList<Vec2i64>::new, LinkedList::addAll, LinkedList::addAll).stream();
    }

    public static Pair<Vec2i64, Vec2i64> getExtents(Collection<Vec2i64> vectors) {
        long minX = Long.MAX_VALUE;
        long maxX = Long.MIN_VALUE;
        long minY = Long.MAX_VALUE;
        long maxY = Long.MIN_VALUE;
        for (Vec2i64 v : vectors) {
            minX = Math.min(minX, v.x);
            maxX = Math.max(maxX, v.x);
            minY = Math.min(minY, v.y);
            maxY = Math.max(maxY, v.y);
        }
        return new Pair<>(new Vec2i64(minX, minY), new Vec2i64(maxX, maxY));
    }

    public Vec2i64 add(Vec2i64 other) {
        return new Vec2i64(this.x + other.x, this.y + other.y);
    }

    public Vec2i64 mul(long f) {
        return new Vec2i64(this.x * f, this.y * f);
    }

    public Vec2i64 mad(long f, Vec2i64 v) {
        return this.mul(f).add(v);
    }

    public long manhattanDistanceTo(Vec2i64 to) {
        return Math.abs(to.x - this.x) + Math.abs(to.y - this.y);
    }
}
