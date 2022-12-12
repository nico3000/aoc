package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Day21 {
    private interface Command {
        public String execute(String source);

        public String invert(String source);
    }

    public record SwapPosition(int posX, int posY) implements Command {
        @Override
        public String execute(String source) {
            int x = Math.min(this.posX, this.posY);
            int y = Math.max(this.posX, this.posY);
            return source.substring(0, x) + source.charAt(y) + source.substring(x + 1, y) + source.charAt(x)
                    + source.substring(y + 1);
        }

        @Override
        public String invert(String source) {
            return this.execute(source);
        }
    }

    public record SwapLetter(char letterX, char letterY) implements Command {
        @Override
        public String execute(String source) {
            char t = '$';
            return source.replace(this.letterX, t).replace(this.letterY, this.letterX).replace(t, this.letterY);
        }

        @Override
        public String invert(String source) {
            return this.execute(source);
        }
    }

    public record Reverse(int posX, int posY) implements Command {
        @Override
        public String execute(String source) {
            return source.substring(0, this.posX)
                    + new StringBuilder(source.substring(this.posX, this.posY + 1)).reverse()
                    + source.substring(this.posY + 1);
        }

        @Override
        public String invert(String source) {
            return this.execute(source);
        }
    }

    public record RotateLeft(int steps) implements Command {
        @Override
        public String execute(String source) {
            int normalizedSteps = ((this.steps % source.length()) + source.length()) % source.length();
            return source.substring(normalizedSteps) + source.substring(0, normalizedSteps);
        }

        @Override
        public String invert(String source) {
            return new RotateLeft(-this.steps).execute(source);
        }
    }

    public record MovePosition(int posX, int posY) implements Command {
        @Override
        public String execute(String source) {
            return new StringBuilder(source).deleteCharAt(this.posX).insert(this.posY, source.charAt(this.posX))
                    .toString();
        }

        @Override
        public String invert(String source) {
            return new MovePosition(this.posY, this.posX).execute(source);
        }
    }

    public record RotateBasedOn(char letter) implements Command {
        @Override
        public String execute(String source) {
            int idx = source.indexOf(this.letter);
            int steps = 1 + idx + (4 <= idx ? 1 : 0);
            return new RotateLeft(-steps).execute(source);
        }

        @Override
        public String invert(String source) {
            int idx = source.indexOf(this.letter);
            for (int i = 0; i < source.length(); ++i) {
                int steps = 1 + i + (4 <= i ? 1 : 0);
                if (source.charAt(i) == new RotateLeft(-steps).execute(source).charAt(idx)) {
                    return new RotateLeft(steps).execute(source);
                }
            }
            throw new RuntimeException("no inverse execution possible");
        }
    }

    public static void main(String[] args) throws IOException {
        List<Command> commands = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day21.class.getResourceAsStream("/2016/day21.txt")))) {
            commands = br.lines().map(l -> {
                String split[] = l.split("\\s+");
                return (Command) switch (split[0] + ' ' + split[1]) {
                    case "swap position" -> new SwapPosition(Integer.valueOf(split[2]), Integer.valueOf(split[5]));
                    case "swap letter" -> new SwapLetter(split[2].charAt(0), split[5].charAt(0));
                    case "reverse positions" -> new Reverse(Integer.valueOf(split[2]), Integer.valueOf(split[4]));
                    case "rotate left" -> new RotateLeft(Integer.valueOf(split[2]));
                    case "rotate right" -> new RotateLeft(-Integer.valueOf(split[2]));
                    case "move position" -> new MovePosition(Integer.valueOf(split[2]), Integer.valueOf(split[5]));
                    case "rotate based" -> new RotateBasedOn(split[6].charAt(0));
                    default -> throw new UnsupportedOperationException(l);
                };
            }).toList();
        }
        String input = "abcdefgh";
        for (Command c : commands) {
            input = c.execute(input);
        }
        System.out.println(input);
        input = "fbgdceah";
        List<Command> reversed = new LinkedList<>(commands);
        Collections.reverse(reversed);
        for (Command c : reversed) {
            input = c.invert(input);
        }
        System.out.println(input);
    }
}