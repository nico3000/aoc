package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.LongAdder;

public class Day23 {
    public static void main(String[] args) throws IOException {
        Machine m;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day23.class.getResourceAsStream("/2017/day23.txt")))) {
            m = new Machine(0, br.lines().toList(), null, null);
        }
        LongAdder adder = new LongAdder();
        m.execute(op -> adder.add(op.cmd().equals("mul") ? 1 : 0));
        System.out.println("Part one: " + adder.sum());
        // m.zeroRegisters();
        // m.setRegister('a', 1);
        // m.execute();
        // System.out.println("Part two: " + m.getRegister('h'));

        int h = 0;
        for (int b = 107900; b <= 124900; b += 17) {
            if (!isPrime(b)) {
                ++h;
            }
        }
        System.out.println("Part two: " + h);
    }

    public static boolean isPrime(int b) {
        if (b % 2 == 0) {
            return false;
        }
        for (int d = 3; d * d <= b; d += 2) {
            if (b % d == 0) {
                return false;
            }
        }
        return true;
    }
}
