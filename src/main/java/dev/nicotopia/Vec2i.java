package dev.nicotopia;

import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Vec2i(int x, int y) {
    public static Stream<Vec2i> streamFromRectangle(int beginX, int beginY, int endX, int endY) {
        return IntStream.range(beginX, endX)
                .mapToObj(x -> IntStream.range(beginY, endY).mapToObj(y -> new Vec2i(x, y)).toList())
                .collect(LinkedList<Vec2i>::new, LinkedList::addAll, LinkedList::addAll).stream();
    }

    public Vec2i add(Vec2i other) {
        return new Vec2i(this.x + other.x, this.y + other.y);
    }

    public Vec2i mul(int f) {
        return new Vec2i(this.x * f, this.y * f);
    }

    public Vec2i mad(int f, Vec2i v) {
        return this.mul(f).add(v);
    }
}
