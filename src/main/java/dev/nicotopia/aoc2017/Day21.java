package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day21 {
    public static void main(String[] args) throws IOException {
        List<int[]> perms2 = new LinkedList<>();
        List<int[]> perms3 = new LinkedList<>();
        int rot2[] = IntStream.range(0, 4).toArray();
        int rot3[] = IntStream.range(0, 9).toArray();
        for (int i = 0; i < 4; ++i) {
            int hor2[] = new int[] { rot2[1], rot2[0], rot2[3], rot2[2] };
            int ver2[] = new int[] { rot2[2], rot2[3], rot2[0], rot2[1] };
            int hor3[] = new int[] { rot3[2], rot3[1], rot3[0], rot3[5], rot3[4], rot3[3], rot3[8], rot3[7], rot3[6] };
            int ver3[] = new int[] { rot3[6], rot3[7], rot3[8], rot3[3], rot3[4], rot3[5], rot3[0], rot3[1], rot3[2] };
            perms2.addAll(Arrays.asList(rot2, hor2, ver2));
            perms3.addAll(Arrays.asList(rot3, hor3, ver3));
            rot2 = new int[] { rot2[1], rot2[3], rot2[0], rot2[2] };
            rot3 = new int[] { rot3[2], rot3[5], rot3[8], rot3[1], rot3[4], rot3[7], rot3[0], rot3[3], rot3[6] };
        }
        Map<String, String> rules = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day21.class.getResourceAsStream("/2017/day21.txt")))) {
            for (String rule[] : br.lines().map(l -> l.split(" => ")).toList()) {
                String srcRows = rule[0].replace("/", "");
                String dstRows = rule[1].replace("/", "");
                for (int perm[] : srcRows.length() == 4 ? perms2 : perms3) {
                    rules.put(IntStream.range(0, perm.length).collect(StringBuffer::new,
                            (sb, i) -> sb.append(srcRows.charAt(perm[i])), StringBuffer::append)
                            .toString(), dstRows);
                }
            }
        }
        char[][] image = new char[][] {
                ".#.".toCharArray(),
                "..#".toCharArray(),
                "###".toCharArray()
        };
        for (int c = 0; c < 18; ++c) {
            char srcImage[][] = image;
            int srcBlockSize;
            if (image.length % 2 == 0) {
                image = new char[3 * image.length / 2][3 * image.length / 2];
                srcBlockSize = 2;
            } else {
                image = new char[4 * image.length / 3][4 * image.length / 3];
                srcBlockSize = 3;
            }
            for (int blockY = 0; blockY < srcImage.length / srcBlockSize; ++blockY) {
                for (int blockX = 0; blockX < srcImage.length / srcBlockSize; ++blockX) {
                    int finalBlockX = blockX;
                    int finalBlockY = blockY;
                    String src = IntStream.range(0, srcBlockSize)
                            .collect(StringBuilder::new,
                                    (sb, i) -> sb.append(srcImage[finalBlockY * srcBlockSize + i],
                                            finalBlockX * srcBlockSize, srcBlockSize),
                                    StringBuilder::append)
                            .toString();
                    String dst = rules.get(src);
                    for (int y = 0; y < srcBlockSize + 1; ++y) {
                        for (int x = 0; x < srcBlockSize + 1; ++x) {
                            image[blockY * (srcBlockSize + 1) + y][blockX * (srcBlockSize + 1) + x] = dst
                                    .charAt(y * (srcBlockSize + 1) + x);
                        }
                    }
                }
            }
            if (c == 4 || c == 17) {
                long sum = Arrays.stream(image).mapToLong(r -> new String(r).chars().filter(ch -> ch == '#').count())
                        .sum();
                System.out.printf("Part %s: %d", c == 4 ? "one" : "two", sum);
            }
        }
    }

    public static void print(char[][] image) {
        Arrays.stream(image).forEach(r -> System.out.println(new String(r)));
    }
}