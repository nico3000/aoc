package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day25 {
    private static boolean found = false;
    private static int count;

    public static void main(String[] args) throws IOException {
        Machine machine;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day25.class.getResourceAsStream("/2016/day25.txt")))) {
            machine = Machine.parseAssembunny(br.lines().toList());
        }
        int targetCount = 100;
        machine.setOutputHandler((m, v) -> {
            if (Day25.count++ % 2 != v) {
                machine.interrupt();
            } else if (Day25.count == targetCount) {
                Day25.found = true;
                machine.interrupt();
            }
        });
        int initValue = -1;
        while (!found) {
            if (++initValue % 1000 == 0) {
                System.out.printf("now checking %d...\n", initValue);
            }
            machine.initialize(initValue, 0, 0, 0);
            Day25.count = 0;
            machine.execute();
        }
        System.out.println("Found: " + initValue);
    }
}
