package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;

public class Day18 {
    private record Cube(int x, int y, int z) {
        public int getSide(int i) { // 0: -x, 1: +x. 2: -y, 3: +y, 4: -z, 5: +z
            return switch (i) {
                case 0 -> (0 & 0x8f) << 24 | (this.z & 0xff) << 16 | (this.y & 0xff) << 8 | (this.x & 0xff);
                case 1 -> (0 & 0x8f) << 24 | (this.z & 0xff) << 16 | (this.y & 0xff) << 8 | (this.x + 1 & 0xff);
                case 2 -> (1 & 0x8f) << 24 | (this.z & 0xff) << 16 | (this.y & 0xff) << 8 | (this.x & 0xff);
                case 3 -> (1 & 0x8f) << 24 | (this.z & 0xff) << 16 | (this.y + 1 & 0xff) << 8 | (this.x & 0xff);
                case 4 -> (2 & 0x8f) << 24 | (this.z & 0xff) << 16 | (this.y & 0xff) << 8 | (this.x & 0xff);
                case 5 -> (2 & 0x8f) << 24 | (this.z + 1 & 0xff) << 16 | (this.y & 0xff) << 8 | (this.x & 0xff);
                default -> throw new IllegalArgumentException();
            };
        }
    }

    public static void main(String[] args) throws IOException {
        List<Cube> cubes;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day18.class.getResourceAsStream("/2022/day18.txt")))) {
            cubes = br.lines().map(l -> l.split(","))
                    .map(s -> new Cube(Integer.valueOf(s[0]), Integer.valueOf(s[1]), Integer.valueOf(s[2]))).toList();
        }
        Set<Integer> faces = new HashSet<>();
        cubes.forEach(c -> IntStream.range(0, 6).map(c::getSide).filter(s -> !faces.remove(s)).forEach(faces::add));
        System.out.println("Part one: " + faces.size());
        int min = cubes.stream().mapToInt(c -> Math.min(Math.min(c.x, c.y), c.z)).min().getAsInt();
        int max = cubes.stream().mapToInt(c -> Math.max(Math.max(c.x, c.y), c.z)).max().getAsInt();
        int size = max + 1 - min;
        Set<Cube> emptyCells = new HashSet<>(IntStream.range(0, size * size * size)
                .mapToObj(i -> new Cube(min + i % size, min + (i / size) % size, min + (i / (size * size))))
                .filter(c -> !cubes.contains(c)).toList());
        Set<Integer> insideFaces = new HashSet<>();
        while (!emptyCells.isEmpty()) {
            Stack<Cube> stack = new Stack<>();
            stack.push(emptyCells.stream().findAny().get());
            boolean inside = true;
            List<Cube> vol = new LinkedList<>();
            while (!stack.isEmpty()) {
                Cube c = stack.pop();
                if (emptyCells.remove(c)) {
                    vol.add(c);
                    inside &= Math.min(Math.min(c.x, c.y), c.z) != min && Math.max(Math.max(c.x, c.y), c.z) != max;
                    if (c.x != min) {
                        stack.push(new Cube(c.x - 1, c.y, c.z));
                    }
                    if (c.y != min) {
                        stack.push(new Cube(c.x, c.y - 1, c.z));
                    }
                    if (c.z != min) {
                        stack.push(new Cube(c.x, c.y, c.z - 1));
                    }
                    if (c.x != max) {
                        stack.push(new Cube(c.x + 1, c.y, c.z));
                    }
                    if (c.y != max) {
                        stack.push(new Cube(c.x, c.y + 1, c.z));
                    }
                    if (c.z != max) {
                        stack.push(new Cube(c.x, c.y, c.z + 1));
                    }
                }
            }
            if (inside) {
                vol.forEach(c -> IntStream.range(0, 6).map(c::getSide).filter(s -> !insideFaces.remove(s))
                        .forEach(insideFaces::add));
            }
        }
        System.out.println("Part two: " + (faces.size() - insideFaces.size()));
    }
}