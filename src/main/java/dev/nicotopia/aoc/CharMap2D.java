package dev.nicotopia.aoc;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.nicotopia.Compass;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.Dijkstra;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class CharMap2D {
    private final char map[][];

    public CharMap2D(int width, int height) {
        this.map = new char[height][width];
    }

    public CharMap2D(char map[][]) {
        this.map = map;
    }

    public int getWidth() {
        return this.map.length == 0 ? 0 : this.map[0].length;
    }

    public int getHeight() {
        return this.map.length;
    }

    public Optional<Vec2i> findAnyPositionOf(char c) {
        return this.coordinates((p, c2) -> c == c2).findAny();
    }

    public void fill(char c) {
        this.coordinates().forEach(p -> this.set(p, c));
    }

    public void set(Vec2i p, char c) {
        this.map[p.y()][p.x()] = c;
    }

    public char get(Vec2i p) {
        return this.map[p.y()][p.x()];
    }

    public Optional<Character> getSafe(Vec2i p) {
        return this.isInBounds(p) ? Optional.of(this.map[p.y()][p.x()]) : Optional.empty();
    }

    public boolean applies(Vec2i p, Predicate<Character> pred) {
        return this.getSafe(p).map(c -> pred.test(c)).orElse(false);
    }

    public boolean is(Vec2i p, char c) {
        return this.isInBounds(p) && this.get(p) == c;
    }

    public boolean isInBounds(Vec2i p) {
        return 0 <= p.y() && p.y() < this.map.length && 0 <= p.x() && p.x() < this.map[p.y()].length;
    }

    public Stream<Vec2i> coordinates() {
        return Vec2i.streamCoordinatesFor(this.map);
    }

    public Stream<Vec2i> coordinates(BiPredicate<Vec2i, Character> filter) {
        return Vec2i.streamCoordinatesFor(this.map).filter(p -> filter.test(p, this.get(p)));
    }

    public OptionalInt getShortestDistance(Vec2i from, Vec2i to, char accessible) {
        return this.getShortestDistances(from, accessible)[to.y()][to.x()];
    }

    public OptionalInt[][] getShortestDistances(Vec2i from, char accessible) {
        OptionalInt distances[][] = new OptionalInt[this.getHeight()][this.getWidth()];
        Runnable reset = () -> this.coordinates().forEach(c -> distances[c.y()][c.x()] = OptionalInt.empty());
        if (!this.is(from, accessible)) {
            reset.run();
            return distances;
        }
        Function<Vec2i, Integer> distanceGetter = p -> distances[p.y()][p.x()].isPresent()
                ? distances[p.y()][p.x()].getAsInt()
                : null;
        ObjIntConsumer<Vec2i> distanceSetter = (p, d) -> distances[p.y()][p.x()] = OptionalInt.of(d);
        Dijkstra.run(distanceGetter, distanceSetter, reset, (p, idx) -> {
            for (Compass c : Compass.values()) {
                if (this.is(p.getNeighbour(c), accessible) && idx-- == 0) {
                    return new NodeDistancePair<Vec2i>(p.getNeighbour(c), 1);
                }
            }
            return null;
        }, from);
        return distances;
    }

    @Override
    public String toString() {
        return Arrays.stream(this.map).map(String::valueOf).collect(Collectors.joining("\n"));
    }
}
