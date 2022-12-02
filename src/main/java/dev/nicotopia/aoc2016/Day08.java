package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day08 {
    private static class Command {
        public enum Type {
            RECT, ROT_ROW, ROT_COL,
        }

        public final Type type;
        public final int v1;
        public final int v2;

        public Command(String raw) {
            String split[] = raw.split(" ", 2);
            this.type = switch (split[0]) {
                case "rect" -> Type.RECT;
                case "rotate" -> switch (split[1].split(" ", 2)[0]) {
                        case "row" -> Type.ROT_ROW;
                        case "column" -> Type.ROT_COL;
                        default -> throw new RuntimeException();
                    };
                default -> throw new RuntimeException();
            };
            MatchResult r = Pattern.compile(switch (this.type) {
                case RECT -> "(\\d+)x(\\d+)";
                default -> "=(\\d+) by (\\d+)";
            }).matcher(split[1]).results().findFirst().get();
            this.v1 = Integer.valueOf(r.group(1));
            this.v2 = Integer.valueOf(r.group(2));
        }
    }

    private static class Screen {
        private final char pixels[][];
        private final char on;

        public Screen(int width, int height, char on, char off) {
            this.pixels = new char[height][width];
            this.on = on;
            Arrays.stream(this.pixels).forEach(row -> Arrays.fill(row, off));
        }

        public void execute(Command c) {
            switch (c.type) {
                case RECT -> this.rect(c.v1, c.v2);
                case ROT_ROW -> this.rotRow(c.v1, c.v2);
                case ROT_COL -> this.rotCol(c.v1, c.v2);
            }
        }

        private void rect(int width, int height) {
            for (int r = 0; r < height; ++r) {
                for (int c = 0; c < width; ++c) {
                    this.pixels[r][c] = on;
                }
            }
        }

        private void rotRow(int row, int count) {
            char dest[] = new char[this.pixels[row].length];
            System.arraycopy(this.pixels[row], 0, dest, count, this.pixels[row].length - count);
            System.arraycopy(this.pixels[row], this.pixels[row].length - count, dest, 0, count);
            this.pixels[row] = dest;
        }

        private void rotCol(int col, int count) {
            for (int c = 0; c < count; ++c) {
                char last = this.pixels[this.pixels.length - 1][col];
                for (int i = this.pixels.length - 1; i != 0; --i) {
                    this.pixels[i][col] = this.pixels[i - 1][col];
                }
                this.pixels[0][col] = last;
            }
        }

        public void print() {
            Arrays.stream(this.pixels).forEach(row -> System.out.println(new String(row)));
        }
    }

    public static void main(String args[]) throws IOException {
        List<Command> commands;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day08.class.getResourceAsStream("/2016/day08.txt")))) {
            commands = br.lines().map(Command::new).toList();
        }
        Screen s = new Screen(50, 6, '#', ' ');
        commands.forEach(s::execute);
        int onPixelsCount = commands.stream().filter(c -> c.type == Command.Type.RECT).mapToInt(c -> c.v1 * c.v2).sum();
        System.out.printf("on pixels: %d\n", onPixelsCount);
        s.print();
    }
}