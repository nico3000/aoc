package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Day21 {
    public static void main(String args[]) throws IOException {
        int board[] = new int[2];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day21.class.getResourceAsStream("/2021/day21.txt")))) {
            board[0] = Integer.valueOf(br.readLine().substring(28)) - 1;
            board[1] = Integer.valueOf(br.readLine().substring(28)) - 1;
        }
        long begin = System.nanoTime();
        long wins[] = new long[2];
        next(0, Arrays.copyOf(board, 2), new int[2], wins, 1);
        int die = 0;
        int scores[] = new int[2];
        while (scores[(die + 1) % 2] < 1000) {
            board[die % 2] += 3 * (die % 100) + 6;
            scores[die % 2] += 1 + board[die % 2] % 10;
            die += 3;
        }
        long end = System.nanoTime();
        System.out.printf("practice score: %d, universe wins: %d, time: %.3f ms\n", scores[die % 2] * die,
                Math.max(wins[0], wins[1]), 1e-6f * (float) (end - begin));
    }

    private static void next(int player, int board[], int scores[], long wins[], long multiplier) {
        for (int amount = 0; amount < 7; ++amount) {
            board[player] += 3 + amount;
            scores[player] += 1 + board[player] % 10;
            long tempMultiplier = multiplier * switch (amount) {
                case 0, 6 -> 1;
                case 1, 5 -> 3;
                case 2, 4 -> 6;
                case 3 -> 7;
                default -> throw new RuntimeException();
            };
            if (scores[player] < 21) {
                next((player + 1) % 2, board, scores, wins, tempMultiplier);
            } else {
                wins[player] += tempMultiplier;
            }
            scores[player] -= 1 + board[player] % 10;
            board[player] -= 3 + amount;
        }
    }
}