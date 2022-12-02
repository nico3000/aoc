package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Day06 {
    public static class FishPopulation {
        private long ages[] = new long[9];
        private int zeroPos = 0;

        public FishPopulation(int... initialCounters) {
            Arrays.stream(initialCounters).forEach(c -> ++this.ages[c]);
        }

        public void update(int days) {
            for (int i = 0; i < days; ++i) {
                this.ages[(this.zeroPos + 7) % this.ages.length] += this.ages[this.zeroPos++ % this.ages.length];
            }
        }

        public long sum() {
            return Arrays.stream(this.ages).sum();
        }
    }

    public static void main(String args[]) throws IOException {
        int startPopulation[] = new int[] { 3, 4, 3, 1, 2 };
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day06.class.getResourceAsStream("/2021/day06.txt")))) {
            startPopulation = Arrays.stream(br.readLine().split(",")).mapToInt(Integer::valueOf).toArray();
        }
        FishPopulation pop = new FishPopulation(startPopulation);
        pop.update(80);
        System.out.printf("After 80 days: %d fish.\n", pop.sum());
        pop.update(256 - 80);
        System.out.printf("After 256 days: %d fish.\n", pop.sum());
    }
}