package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.IntStream;

public class Day17 {
    private record State(int windIdx, int shapeIdx) {
    }

    private record IterationHeight(int iteration, int height) {
    }

    private static final char shapes[][][] = {
            {
                    "####".toCharArray(),
            },
            {
                    " # ".toCharArray(),
                    "###".toCharArray(),
                    " # ".toCharArray(),
            },
            {
                    "  #".toCharArray(),
                    "  #".toCharArray(),
                    "###".toCharArray(),
            },
            {
                    "#".toCharArray(),
                    "#".toCharArray(),
                    "#".toCharArray(),
                    "#".toCharArray(),
            },
            {
                    "##".toCharArray(),
                    "##".toCharArray(),
            },
    };

    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day17.class.getResourceAsStream("/2022/day17.txt")))) {
            winds = br.readLine();
        }
        // winds = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
        StringBuilder cave[] = new StringBuilder[7];
        IntStream.range(0, cave.length).forEach(i -> cave[i] = new StringBuilder());
        for (int i = 0; i < 2022; ++i) {
            step(cave);
        }
        System.out.println("Part one: " + (cave[0].length() + rowsDeleted));
        windIdx = 0;
        shapeIdx = 0;
        rowsDeleted = 0;
        Arrays.stream(cave).forEach(sb -> sb.setLength(0));
        Map<State, IterationHeight> resetStates = new HashMap<>();
        long maxRockCount = 1000000000000L;
        IterationHeight ih0;
        IterationHeight ih1;
        for (int i = 0;; ++i) {
            if (cave[0].length() == 0) {
                State s = new State(windIdx, shapeIdx);
                ih0 = resetStates.put(s, new IterationHeight(i, (int) rowsDeleted));
                if (ih0 != null) {
                    ih1 = new IterationHeight(i, (int) rowsDeleted);
                    System.out.printf("We had that state before: wind %d, shape %d, i0=%d, i1=%d\n", s.windIdx,
                            s.shapeIdx, ih0.iteration, ih1.iteration);
                    break;
                }
            }
            step(cave);
        }
        long iterationBlockLen = (long) (ih1.iteration - ih0.iteration);
        long iterationBlockHeightDelta = (long) (ih1.height - ih0.height);
        long iterationBlockCount = (maxRockCount - (long) ih0.iteration) / iterationBlockLen;
        int remainingIterations = (int) ((maxRockCount - (long) ih0.iteration) % iterationBlockLen);
        rowsDeleted = (long) ih0.height + iterationBlockHeightDelta * iterationBlockCount;
        for (int i = 0; i < remainingIterations; ++i) {
            step(cave);
        }
        System.out.println("Part two: " + (cave[0].length() + rowsDeleted));
    }

    private static String winds;
    private static int windIdx = 0;
    private static int shapeIdx = 0;
    private static long rowsDeleted = 0;

    public static void step(StringBuilder cave[]) {
        char[][] shape = shapes[shapeIdx];
        shapeIdx = (int) (shapeIdx + 1) % shapes.length;
        int y = cave[0].length() + 3;
        int x = 2;
        do {
            char wind = winds.charAt(windIdx);
            windIdx = (windIdx + 1) % winds.length();
            int newX = wind == '<' ? x - 1 : x + 1;
            if (0 <= newX && newX + shape[0].length <= cave.length && canBeInserted(cave, newX, y, shape)) {
                x = newX;
            }
            --y;
        } while (0 <= y && canBeInserted(cave, x, y, shape));
        insert(cave, x, y + 1, shape);
        rowsDeleted += checkCave(cave, y + 1);
    }

    public static void printCave(StringBuilder cave[]) {
        System.out.println("Cave");
        IntStream.range(0, cave[0].length())
                .mapToObj(
                        i -> Arrays.stream(cave).map(sb -> sb.charAt(sb.length() - 1 - i)).map(c -> c == '#' ? c : '.')
                                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString())
                .forEach(l -> System.out.println("|" + l + "|"));
        System.out.println("+" + IntStream.range(0, cave.length).collect(StringBuilder::new, (sb, i) -> sb.append('-'),
                StringBuilder::append) + "+");
    }

    private static long checkCave(StringBuilder cave[], int minY) {
        for (int y = minY; y < cave[0].length(); ++y) {
            LongAccumulator rowToDelete = new LongAccumulator(Math::min, y);
            for (int x = 0; rowToDelete.get() != -1 && x < cave.length; ++x) {
                if (cave[x].charAt(y) != '#') {
                    if (y == 0 || cave[x].charAt(y - 1) != '#') {
                        rowToDelete.accumulate(-1);
                    } else if (cave[x].charAt(y - 1) == '#') {
                        rowToDelete.accumulate(y - 1);
                    }
                }
            }
            if (rowToDelete.get() != -1) {
                Arrays.stream(cave).forEach(sb -> sb.delete(0, rowToDelete.intValue() + 1));
                return rowToDelete.get() + 1;
            }
        }
        return 0;
    }

    private static boolean canBeInserted(StringBuilder cave[], int posX, int posY, char shape[][]) {
        for (int y = 0; y < shape.length; ++y) {
            for (int x = 0; x < shape[y].length; ++x) {
                if (posY + y < cave[posX + x].length() && cave[posX + x].charAt(posY + y) == '#'
                        && shape[shape.length - 1 - y][x] == '#') {
                    return false;
                }
            }
        }
        return true;
    }

    private static void insert(StringBuilder cave[], int posX, int posY, char shape[][]) {
        Arrays.stream(cave).forEach(sb -> sb.setLength(Math.max(sb.length(), posY + shape.length)));
        for (int y = 0; y < shape.length; ++y) {
            for (int x = 0; x < shape[y].length; ++x) {
                if (shape[shape.length - 1 - y][x] == '#') {
                    cave[posX + x].setCharAt(posY + y, '#');
                }
            }
        }
    }
}