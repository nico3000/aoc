package dev.nicotopia;

import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Coordinate(int x, int y) {
    public static Stream<Coordinate> streamFromRectangle(int beginX, int beginY, int endX, int endY) {
        return IntStream.range(beginX, endX)
                .mapToObj(x -> IntStream.range(beginY, endY).mapToObj(y -> new Coordinate(x, y)).toList())
                .collect(LinkedList<Coordinate>::new, LinkedList::addAll, LinkedList::addAll).stream();
    }
}
