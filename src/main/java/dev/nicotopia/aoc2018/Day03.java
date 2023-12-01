package dev.nicotopia.aoc2018;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day03 {
    private record Position(int x, int y) {
    }

    private record Rectangle(int id, int x, int y, int width, int height) {
        public boolean contains(Position p) {
            return this.x <= p.x && p.x < this.x + this.width && this.y <= p.y && p.y < this.y + this.height;
        }

        public boolean overlaps(Rectangle other) {
            return !(this.x + this.width <= other.x || other.x + other.width <= this.x
                    || this.y + this.height <= other.y || other.y + other.height <= this.y);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Rectangle> claims;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day03.class.getResourceAsStream("/2018/day03.txt")))) {
            Pattern p = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");
            claims = br.lines().map(p::matcher).filter(Matcher::matches)
                    .map(m -> new Rectangle(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)),
                            Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)), Integer.valueOf(m.group(5))))
                    .toList();
        }
        int minX = claims.stream().mapToInt(Rectangle::x).min().getAsInt();
        int minY = claims.stream().mapToInt(Rectangle::y).min().getAsInt();
        int maxX = claims.stream().mapToInt(r -> r.x + r.width).max().getAsInt();
        int maxY = claims.stream().mapToInt(r -> r.y + r.height).max().getAsInt();
        Stream<Position> positions = IntStream.range(minX, maxX).mapToObj(x -> x)
                .flatMap(x -> IntStream.range(minY, maxY).mapToObj(y -> new Position(x, y)));
        long count = positions.filter(p -> 1 < claims.stream().filter(c -> c.contains(p)).count()).count();
        System.out.println("Part one: " + count);
        Rectangle r = claims.stream().filter(c1 -> claims.stream().noneMatch(c2 -> c1 != c2 && c1.overlaps(c2)))
                .findAny().get();
        System.out.println("Part two: " + r.id);
    }
}
