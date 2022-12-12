package dev.nicotopia.aoc2016;

import java.util.stream.IntStream;

public class Day19 {
    public static void main(String[] args) {
        int elfCount = 3014603;
        int elves[] = IntStream.range(1, elfCount + 1).toArray();
        while (elves.length != 1) {
            int newElves[] = new int[elves.length / 2];
            for (int i = 0; i < newElves.length; ++i) {
                newElves[i] = elves[elves.length % 2 == 0 ? 2 * i : 2 * i + 2];
            }
            elves = newElves;
        }
        System.out.println("Last remaining elf, part one: " + elves[0]);
        elves = IntStream.range(1, elfCount + 1).toArray();
        int beg = 0;
        int end = elves.length;
        int next = 0;
        long beginTime = System.currentTimeMillis();
        int lastOut = 0;
        while (end - beg != 1) {
            int len = end - beg;
            int toRemove = (next + len / 2) % len;
            if (toRemove < len / 2) {
                if (toRemove != 0) {
                    System.arraycopy(elves, beg, elves, beg + 1, toRemove);
                }
                ++beg;
            } else {
                if (toRemove != len - 1) {
                    System.arraycopy(elves, beg + toRemove + 1, elves, beg + toRemove, len - toRemove - 1);
                }
                --end;
            }
            if (next < toRemove) {
                ++next;
            }
            next %= (len - 1);
            float percentage = 1.0f - (float) (len - 1) / (float) elfCount;
            if ((int) (1000.0f * percentage) != lastOut) {
                long timeTaken = System.currentTimeMillis() - beginTime;
                long totalTime = (long) ((float) timeTaken / percentage);
                long remaining = totalTime - timeTaken;
                lastOut = (int) (1000.0f * percentage);
                System.out.printf("%3.1f%%, time taken: %d s, estimated total time: %s s, eta: %d s\n",
                        (float) lastOut / 10.0f, timeTaken / 1000, totalTime / 1000, remaining / 1000);
            }
        }
        System.out.println("Last remaining elf, part two: " + elves[beg]);
    }
}
