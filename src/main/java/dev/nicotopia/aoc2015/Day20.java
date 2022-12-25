package dev.nicotopia.aoc2015;

import java.util.Arrays;

public class Day20 {
    public static void main(String[] args) {
        long input = 36000000;
        for (int n = 1; true; ++n) {
            if (input <= 10 * getDivisorSum(n)) {
                System.out.println("Part one: " + n);
                break;
            }
        }
        int houses[] = new int[1];
        int minIdx = Integer.MAX_VALUE;
        for (int e = 1; e <= minIdx; ++e) {
            int h = e;
            for (int i = 0; i < 50; ++i) {
                if (houses.length <= h) {
                    houses = Arrays.copyOf(houses, 2 * houses.length);
                }
                houses[h] += 11 * e;
                if (input <= houses[h]) {
                    minIdx = Math.min(minIdx, h);
                }
                h += e;
            }
        }
        System.out.println("Part two " + minIdx);
    }

    private static final int getDivisorSum(int n) {
        int sum = 0;
        for (int i = 1; i * i <= n; ++i) {
            if (n % i == 0) {
                sum += i * i == n ? i : i + n / i;
            }
        }
        return sum;
    }
}