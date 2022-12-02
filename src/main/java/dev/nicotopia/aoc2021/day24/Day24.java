package dev.nicotopia.aoc2021.day24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev.nicotopia.aoc2021.day24.Alu.Register;

public class Day24 {
    public static void main(String args[]) throws IOException {
        System.out.println(-1 % 3);
        Alu alu = new Alu();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day24.class.getResourceAsStream("/2021/day24.txt")))) {
            alu.setProgram(br.lines().toArray(String[]::new));
        }
        alu.reset(1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5);
        do {
            alu.execute();
            alu.print();
            if (alu.get(Register.Z) == 0) {
                System.out.println("this!");
            }

        } while (subOne(alu, alu.getInputCount() - 1));
    }

    public static boolean subOne(Alu alu, int pos) {
        if (pos == 0 && alu.getInput(pos) == 1) {
            return false;
        }
        if (alu.getInput(pos) == 1) {
            alu.setInput(pos, 9);
            return subOne(alu, pos - 1);
        } else {
            alu.setInput(pos, alu.getInput(pos) - 1);
            return true;
        }
    }
}
