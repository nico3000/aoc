package dev.nicotopia.aoc2017;

public class Day15 {
    private static class Generator {
        private final long factor;
        private final long mod;
        private long prev;

        public Generator(long factor, long mod, long start) {
            this.factor = factor;
            this.mod = mod;
            this.prev = start;
        }

        public long next() {
            do {
                this.prev = (this.prev * this.factor) % 2147483647L;
            } while (this.prev % this.mod != 0);
            return this.prev;
        }
    }

    public static void main(String[] args) {
        int partOne = execute(new Generator(16807, 1, 783), new Generator(48271, 1, 325), 40000000);
        int partTwo = execute(new Generator(16807, 4, 783), new Generator(48271, 8, 325), 5000000);
        System.out.printf("Part one: %d, part two: %d\n", partOne, partTwo);
    }

    private static int execute(Generator genA, Generator genB, long iterations) {
        int count = 0;
        for (long i = 0; i < iterations; ++i) {
            if ((genA.next() & ((1 << 16) - 1)) == (genB.next() & ((1 << 16) - 1))) {
                ++count;
            }
        }
        return count;
    }
}
