package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Day20 {
    private static record Position(int x, int y) {
    }

    private static class Image {
        private int[][] data;
        private int[][] tempData;
        private int outsideValue;

        public Image(int data[][], int border, int outsideValue) {
            this.data = new int[data.length + 2 * border][data[0].length + 2 * border];
            this.outsideValue = outsideValue;
            for (int y = 0; y < border; ++y) {
                Arrays.fill(this.data[y], outsideValue);
                Arrays.fill(this.data[this.data.length - 1 - y], outsideValue);
            }
            for (int y = 0; y < data.length; ++y) {
                Arrays.fill(this.data[y + border], 0, border, outsideValue);
                Arrays.fill(this.data[y + border], this.data[0].length - border, this.data[0].length, outsideValue);
                System.arraycopy(data[y], 0, this.data[y + border], border, data[0].length);
            }
            this.tempData = new int[this.data.length][this.data[0].length];
        }

        public int getValue(int x, int y) {
            return 0 <= x && x < this.getWidth() && 0 <= y && y < this.getHeight() ? this.data[y][x]
                    : this.outsideValue;
        }

        public int getWidth() {
            return this.data[0].length;
        }

        public int getHeight() {
            return this.data.length;
        }

        public void enhance(String algo) {
            IntStream.range(0, this.getWidth() * this.getHeight())
                    .mapToObj(i -> new Position(i % this.getWidth(), i / this.getWidth())).forEach(p -> {
                        int v = this.getValue(p.x - 1, p.y - 1) << 8 | this.getValue(p.x, p.y - 1) << 7
                                | this.getValue(p.x + 1, p.y - 1) << 6 | this.getValue(p.x - 1, p.y) << 5
                                | this.getValue(p.x, p.y) << 4 | this.getValue(p.x + 1, p.y) << 3
                                | this.getValue(p.x - 1, p.y + 1) << 2 | this.getValue(p.x, p.y + 1) << 1
                                | this.getValue(p.x + 1, p.y + 1);
                        this.tempData[p.y][p.x] = algo.charAt(v) == '#' ? 1 : 0;
                    });
            this.outsideValue = algo.charAt(this.outsideValue == 1 ? algo.length() - 1 : 0) == '#' ? 1 : 0;
            int t[][] = this.tempData;
            this.tempData = this.data;
            this.data = t;
        }

        public int getLitCount() {
            return Arrays.stream(this.data).mapToInt(r -> Arrays.stream(r).sum()).sum();
        }
    }

    public static void main(String args[]) throws IOException {
        String algo;
        Image image;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day20.class.getResourceAsStream("/2021/day20.txt")))) {
            algo = br.readLine();
            br.readLine();
            image = new Image(br.lines().map(l -> l.chars().map(c -> c == '#' ? 1 : 0).toArray()).toArray(int[][]::new),
                    51, 0);
        }
        long begin = System.nanoTime();
        image.enhance(algo);
        image.enhance(algo);
        int twiceLitCount = image.getLitCount();
        for (int i = 2; i < 50; ++i) {
            image.enhance(algo);
        }
        int litCount = image.getLitCount();
        long end = System.nanoTime();
        System.out.printf("lit count, after 2: %d, after 50: %d, time: %.3f ms\n", twiceLitCount, litCount,
                1e-6f * (float) (end - begin));
    }
}