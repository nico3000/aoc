package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Day04 {
    private static class BingoBoard {
        private final int values[][] = new int[5][5];
        private final boolean hits[][] = new boolean[5][5];

        public void setRow(int r, int values[]) {
            this.values[r] = values;
        }

        public int hit(int value) {
            int sum = 0;
            boolean won = false;
            for (int r = 0; r < 5; ++r) {
                for (int c = 0; c < 5; ++c) {
                    if (this.values[r][c] == value) {
                        this.hits[r][c] = true;
                        won = this.hasWon(r, c);
                    } else if (!this.hits[r][c]) {
                        sum += this.values[r][c];
                    }
                }
            }
            return won ? value * sum : -1;
        }

        private boolean hasWon(int r, int c) {
            return (this.hits[r][0] && this.hits[r][1] && this.hits[r][2] && this.hits[r][3] && this.hits[r][4]) ||
                    (this.hits[0][c] && this.hits[1][c] && this.hits[2][c] && this.hits[3][c] && this.hits[4][c]);
        }
    }

    public static void main(String args[]) throws IOException {
        List<Integer> numbers = new LinkedList<>();
        List<BingoBoard> boards = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day04.class.getResourceAsStream("/2021/day04.txt")))) {
            numbers = Arrays.stream(br.readLine().split(",")).mapToInt(Integer::valueOf).boxed().toList();
            while (br.readLine() != null) {
                BingoBoard board = new BingoBoard();
                for (int i = 0; i < 5; ++i) {
                    board.setRow(i,
                            Arrays.stream(br.readLine().trim().split("\\s+")).mapToInt(Integer::valueOf).toArray());
                }
                boards.add(board);
            }
        }
        for (int v : numbers) {
            Iterator<BingoBoard> it = boards.iterator();
            while (it.hasNext()) {
                int score = it.next().hit(v);
                if (score != -1) {
                    System.out.printf("Score: %d\n", score);
                    it.remove();
                }
            }
        }
    }
}