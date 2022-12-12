package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Day10 {
    public static void main(String[] args) throws IOException {
        String line;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day10.class.getResourceAsStream("/2017/day10.txt")))) {
            line = br.readLine();
        }
        KnotHasher hasher = new KnotHasher();
        byte[] dataPartOne = hasher.hashOneRound(Arrays.stream(line.split(",")).map(Integer::valueOf).toList());
        System.out.println("Part one: " + Byte.toUnsignedInt(dataPartOne[0]) * Byte.toUnsignedInt(dataPartOne[1]));
        System.out.println("Part two: " + hasher.toHexString(hasher.hash(line.getBytes())));
    }
}