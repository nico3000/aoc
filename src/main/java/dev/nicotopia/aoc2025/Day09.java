package dev.nicotopia.aoc2025;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day09 extends DayBase {
    private record VerticalEdge(int x, int yBegin, int yEnd) {
        public boolean intersectsHorizontalScanline(int y) {
            return Math.min(this.yBegin, this.yEnd) <= y && y <= Math.max(this.yBegin, this.yEnd);
        }

        public boolean contains(int x, int y) {
            return x == this.x && this.intersectsHorizontalScanline(y);
        }
    }

    private record HorizontalEdge(int y, int xBegin, int xEnd) {
        public boolean intersectsVerticalScanline(int x) {
            return Math.min(this.xBegin, this.xEnd) <= x && x <= Math.max(this.xBegin, this.xEnd);
        }

        public boolean contains(int x, int y) {
            return y == this.y && this.intersectsVerticalScanline(x);
        }
    }

    private record Rectangle(Vec2i a, Vec2i b) {
        private long area() {
            long dx = Math.abs((a.x() - b.x())) + 1;
            long dy = Math.abs((a.y() - b.y())) + 1;
            return dx * dy;
        }
    }

    private final List<HorizontalEdge> horizontalEdges = new ArrayList<>();
    private final List<VerticalEdge> verticalEdges = new ArrayList<>();
    private final List<Rectangle> rectangles = new ArrayList<>();

    private void processInput() {
        List<Vec2i> vertices = this.getPrimaryPuzzleInput().stream().map(line -> line.split(","))
                .map(s -> new Vec2i(Integer.valueOf(s[0]), Integer.valueOf(s[1]))).toList();
        for (int i = 0; i < vertices.size(); ++i) {
            Vec2i a = vertices.get(i);
            Vec2i b = vertices.get((i + 1) % vertices.size());
            if (a.x() == b.x()) {
                this.verticalEdges.add(new VerticalEdge(a.x(), a.y(), b.y()));
            } else if (a.y() == b.y()) {
                this.horizontalEdges.add(new HorizontalEdge(a.y(), a.x(), b.x()));
            }
            for (int j = i + 1; j < vertices.size(); ++j) {
                this.rectangles.add(new Rectangle(a, vertices.get(j)));
            }
        }
        Collections.sort(this.rectangles, (a, b) -> Long.compare(b.area(), a.area()));
    }

    private boolean isInside(int x, int y) {
        for (HorizontalEdge edge : this.horizontalEdges) {
            if (edge.contains(x, y)) {
                return true;
            }
        }
        int leftEdgeCount = 0;
        for (VerticalEdge edge : this.verticalEdges) {
            if (edge.contains(x, y)) {
                return true;
            }
            int minY = Math.min(edge.yBegin, edge.yEnd);
            int maxY = Math.max(edge.yBegin, edge.yEnd);
            if (minY <= y && y < maxY && edge.x < x) {
                ++leftEdgeCount;
            }
        }
        return leftEdgeCount % 2 == 1;
    }

    private boolean isInside(Rectangle r) {
        int minX = Math.min(r.a.x(), r.b.x());
        int maxX = Math.max(r.a.x(), r.b.x());
        if (IntStream.range(minX, maxX).parallel()
                .anyMatch(x -> !this.isInside(x, r.a.y()) || !this.isInside(x, r.b.y()))) {
            return false;
        }
        int minY = Math.min(r.a.y(), r.b.y());
        int maxY = Math.max(r.a.y(), r.b.y());
        if (IntStream.range(minY, maxY).parallel()
                .anyMatch(y -> !this.isInside(r.a.x(), y) || !this.isInside(r.b.x(), y))) {
            return false;
        }
        return true;
    }

    private long partOne() {
        return this.rectangles.getFirst().area();
    }

    private long partTwo() {
        return this.rectangles.stream().filter(this::isInside).findFirst().get().area();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
