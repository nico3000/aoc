package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day23 {
    private static Machine getMachine(int regA, int regB, int regC, int regD) throws IOException {
        Machine machine;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day12.class.getResourceAsStream("/2016/day23.txt")))) {
            machine = Machine.parseAssembunny(br.lines().toList());
        }
        machine.initialize(regA, regB, regC, regD);
        return machine;
    }

    public static void main(String args[]) throws IOException {
        Machine machinePartOne = getMachine(7, 0, 0, 0);
        machinePartOne.execute();
        Machine machinePartTwo = getMachine(12, 0, 0, 0);
        machinePartTwo.execute();
        System.out.printf("register a; part one: %d, part two: %d", machinePartOne.regA(), machinePartTwo.regA());
    }
}
