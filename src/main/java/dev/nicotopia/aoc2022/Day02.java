package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day02 {
    public static void main(String[] args) throws IOException {
        int part1Score = 0;
        int part2Score = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day02.class.getResourceAsStream("/2022/day02.txt")))) {
            for (String draw[] : br.lines().map(l -> l.split("\\s+")).toList()) {
                int opp = draw[0].charAt(0) - 'A';
                int part1Me = draw[1].charAt(0) - 'X';
                int part2Me = (part1Me + opp + 2) % 3;
                part1Score += 1 + part1Me + 3 * ((4 + part1Me - opp) % 3);
                part2Score += 1 + part2Me + ((part2Me == (opp + 1) % 3) ? 6 : (part2Me == opp) ? 3 : 0);
            }
        }
        System.out.printf("Score part 1: %d\nScore part 2: %d\n", part1Score, part2Score);
    }
}