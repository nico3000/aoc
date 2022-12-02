package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day05 {
    private record Point(int x, int y) {
    }

    private static class Line implements Iterable<Point> {
        public enum Type {
            HORIZONTAL,
            VERTICAL,
            DIAGONAL,
            WILD,
        }

        private final Point p0;
        private final Point p1;

        public Line(int x0, int y0, int x1, int y1) {
            if (x0 == x1) {
                this.p0 = y0 < y1 ? new Point(x0, y0) : new Point(x1, y1);
                this.p1 = y0 < y1 ? new Point(x1, y1) : new Point(x0, y0);
            } else {
                this.p0 = x0 < x1 ? new Point(x0, y0) : new Point(x1, y1);
                this.p1 = x0 < x1 ? new Point(x1, y1) : new Point(x0, y0);
            }
        }

        public Type getType() {
            if (this.p0.x == this.p1.x) {
                return Type.VERTICAL;
            } else if (this.p0.y == this.p1.y) {
                return Type.HORIZONTAL;
            } else if (this.p1.x - this.p0.x == Math.abs(this.p1.y - this.p0.y)) {
                return Type.DIAGONAL;
            } else {
                return Type.WILD;
            }
        }

        @Override
        public Iterator<Point> iterator() {
            return new Iterator<Point>() {
                private int i = 0;

                @Override
                public boolean hasNext() {
                    return switch (Line.this.getType()) {
                        case HORIZONTAL, DIAGONAL -> Line.this.p0.x + this.i <= Line.this.p1.x;
                        case VERTICAL -> Line.this.p0.y + this.i <= Line.this.p1.y;
                        default -> false;
                    };
                }

                @Override
                public Point next() {
                    Point p = switch (Line.this.getType()) {
                        case HORIZONTAL -> new Point(Line.this.p0.x + i, Line.this.p0.y);
                        case VERTICAL -> new Point(Line.this.p0.x, Line.this.p0.y + i);
                        case DIAGONAL -> new Point(Line.this.p0.x + i,
                                Line.this.p0.y + (Line.this.p0.y < Line.this.p1.y ? i : -i));
                        default -> null;
                    };
                    ++this.i;
                    return p;
                }
            };
        }
    }

    private static class Grid {
        private final Map<Point, Integer> grid = new HashMap<>();

        public void mark(Point p) {
            Integer old = this.grid.get(p);
            this.grid.put(p, old != null ? old + 1 : 1);
        }

        public long count(int minValue) {
            return this.grid.values().stream().filter(v -> minValue <= v).count();
        }
    }

    public static void main(String args[]) throws IOException {
        List<Line> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day05.class.getResourceAsStream("/2021/day05.txt")))) {
            Pattern p = Pattern.compile("^(\\d+),(\\d+) -> (\\d+),(\\d+)$");
            lines = br.lines().collect(LinkedList::new, (list, line) -> {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int x0 = Integer.valueOf(m.group(1));
                    int y0 = Integer.valueOf(m.group(2));
                    int x1 = Integer.valueOf(m.group(3));
                    int y1 = Integer.valueOf(m.group(4));
                    list.add(new Line(x0, y0, x1, y1));
                }
            }, LinkedList::addAll);
        }
        Grid grid = new Grid();
        lines.forEach(line -> {
            //if (line.getType() == Line.Type.HORIZONTAL || line.getType() == Line.Type.VERTICAL) {
                for (Point p : line) {
                    grid.mark(p);
                }
            //}
        });
        System.out.println(grid.count(2));
    }
}