package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Day19 {
    private static record Position(int x, int y, int z) {
        public Position rotate(int rotIdx) {
            return switch (rotIdx) {
                case 0 -> new Position(x, y, z);
                case 1 -> new Position(x, -z, y);
                case 2 -> new Position(x, -y, -z);
                case 3 -> new Position(x, z, -y);
                case 6 -> new Position(-x, z, y);
                case 5 -> new Position(-x, -y, z);
                case 4 -> new Position(-x, -z, -y);
                case 7 -> new Position(-x, y, -z);

                case 8 -> new Position(y, z, x);
                case 9 -> new Position(y, -x, z);
                case 10 -> new Position(y, -z, -x);
                case 11 -> new Position(y, x, -z);
                case 12 -> new Position(-y, x, z);
                case 13 -> new Position(-y, -z, x);
                case 14 -> new Position(-y, -x, -z);
                case 15 -> new Position(-y, z, -x);

                case 16 -> new Position(z, x, y);
                case 17 -> new Position(z, -y, x);
                case 18 -> new Position(z, -x, -y);
                case 19 -> new Position(z, y, -x);
                case 20 -> new Position(-z, y, x);
                case 21 -> new Position(-z, -x, y);
                case 22 -> new Position(-z, -y, -x);
                case 23 -> new Position(-z, x, -y);
                default -> throw new RuntimeException();
            };
        }

        public Position subtract(Position other) {
            return new Position(this.x - other.x, this.y - other.y, this.z - other.z);
        }

        public int manhattenDistanceTo(Position other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z);
        }
    }

    private static class DetectionResult {
        private List<Integer> ids = new LinkedList<>();
        private Set<Position> scanners = new HashSet<>();
        private Set<Position> beacons = new HashSet<>();

        public DetectionResult(int id) {
            this.ids.add(id);
            this.scanners.add(new Position(0, 0, 0));
        }

        public boolean merge(DetectionResult other, int threshold) {
            for (int r = 0; r < 24; ++r) {
                final int _r = r;
                List<Position> otherRotatedBeacons = other.beacons.stream().map(b -> b.rotate(_r)).toList();
                for (Position p1 : this.beacons) {
                    for (Position p2 : otherRotatedBeacons) {
                        Position offset = p2.subtract(p1);
                        List<Position> notMatched = otherRotatedBeacons.stream().map(b -> b.subtract(offset))
                                .filter(b -> !this.beacons.contains(b)).toList();
                        if (threshold <= other.beacons.size() - notMatched.size()) {
                            this.ids.addAll(other.ids);
                            this.beacons.addAll(notMatched);
                            this.scanners
                                    .addAll(other.scanners.stream().map(s -> s.rotate(_r).subtract(offset)).toList());
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public String toString() {
            return String.join(",", this.ids.stream().map(String::valueOf).toList());
        }
    }

    public static void main(String args[]) throws IOException {
        LinkedList<DetectionResult> results = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day19.class.getResourceAsStream("/2021/day19.txt")))) {
            br.lines().forEach(line -> {
                if (line.startsWith("---")) {
                    results.add(new DetectionResult(results.size()));
                } else if (!line.isBlank()) {
                    String split[] = line.split(",");
                    results.getLast().beacons.add(new Position(Integer.valueOf(split[0]), Integer.valueOf(split[1]),
                            Integer.valueOf(split[2])));
                }
            });
        }
        long begin = System.nanoTime();
        boolean merged;
        do {
            merged = false;
            DetectionResult base = results.removeLast();
            Iterator<DetectionResult> iter = results.iterator();
            while (iter.hasNext()) {
                if (base.merge(iter.next(), 12)) {
                    iter.remove();
                    merged = true;
                }
            }
            results.addLast(base);
        } while (merged);
        long end = System.nanoTime();
        if (results.size() != 1) {
            throw new RuntimeException();
        }
        int max = 0;
        for (Position s1 : results.getFirst().scanners) {
            for (Position s2 : results.getFirst().scanners) {
                max = Math.max(max, s1.manhattenDistanceTo(s2));
            }
        }
        System.out.printf("beacons: %d, max distance: %d, time: %.3f ms\n", results.getFirst().beacons.size(), max,
                1e-6f * (float) (end - begin));
    }
}