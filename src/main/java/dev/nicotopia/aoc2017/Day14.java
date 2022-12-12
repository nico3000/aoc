package dev.nicotopia.aoc2017;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;

public class Day14 {
    private record Position(int x, int y) {
    }

    public static void main(String[] args) {
        String input = "amgozmfv";
        int gridSize = 128;
        Set<Position> data = new HashSet<>();
        for (int y = 0; y < gridSize; ++y) {
            int finalY = y;
            byte hash[] = new KnotHasher().hash((input + "-" + y).getBytes());
            for (int i = 0; i < hash.length; ++i) {
                String bits = Integer.toBinaryString(Byte.toUnsignedInt(hash[i]));
                int offset = 8 * i + 8 - bits.length();
                IntStream.range(0, bits.length()).filter(j -> bits.charAt(j) == '1')
                        .mapToObj(j -> new Position(offset + j, finalY)).forEach(data::add);
            }
        }
        System.out.println("Part one: " + data.size());
        int regionCount = 0;
        while (!data.isEmpty()) {
            Stack<Position> stack = new Stack<>();
            data.stream().findAny().filter(data::remove).ifPresent(stack::push);
            while (!stack.isEmpty()) {
                Position p = stack.pop();
                IntStream.range(0, 4).mapToObj(i -> switch (i) {
                    case 0 -> new Position(p.x - 1, p.y);
                    case 1 -> new Position(p.x + 1, p.y);
                    case 2 -> new Position(p.x, p.y - 1);
                    case 3 -> new Position(p.x, p.y + 1);
                    default -> throw new RuntimeException();
                }).filter(data::remove).forEach(stack::push);
            }
            ++regionCount;
        }
        System.out.println("Part two: " + regionCount);
    }
}