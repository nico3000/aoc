package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Day05 {
    private record Move(int count, int from, int to) {
        public void execute(List<Stack<Character>> stacks) {
            for (int i = 0; i < this.count; ++i) {
                stacks.get(this.to).push(stacks.get(this.from).pop());
            }
        }

        public void executeAtOnce(List<Stack<Character>> stacks) {
            Stack<Character> temp = new Stack<>();
            for (int i = 0; i < this.count; ++i) {
                temp.push(stacks.get(this.from).pop());
            }
            for (int i = 0; i < this.count; ++i) {
                stacks.get(this.to).push(temp.pop());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> stackLines = new LinkedList<>();
        List<Move> moves = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day05.class.getResourceAsStream("/2022/day05.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.indexOf('[') != -1) {
                    stackLines.add(0, line);
                } else if (line.startsWith("move")) {
                    String split[] = line.split("\\s+");
                    moves.add(new Move(Integer.valueOf(split[1]), Integer.valueOf(split[3]) - 1,
                            Integer.valueOf(split[5]) - 1));
                }
            }
        }
        List<Stack<Character>> stacks9000 = buildStacks(stackLines);
        printStacks(stacks9000);
        moves.forEach(m -> m.execute(stacks9000));
        printStacks(stacks9000);
        List<Stack<Character>> stacks9001 = buildStacks(stackLines);
        moves.forEach(m -> m.executeAtOnce(stacks9001));
        printStacks(stacks9001);
        System.out.printf("CrateMover 9000: %s, CrateMover 9001: %s\n", getTops(stacks9000), getTops(stacks9001));
    }

    private static List<Stack<Character>> buildStacks(List<String> stackLines) {
        List<Stack<Character>> stacks = new ArrayList<>();
        for (String stackLine : stackLines) {
            for (int i = 0; i < stackLine.length(); i += 4) {
                if (stacks.size() <= i / 4) {
                    stacks.add(new Stack<>());
                }
                char c = stackLine.charAt(i + 1);
                if (!Character.isWhitespace(c)) {
                    stacks.get(i / 4).add(c);
                }
            }
        }
        return stacks;
    }

    private static String getTops(List<Stack<Character>> stacks) {
        return stacks.stream().map(Stack::peek)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private static void printStacks(List<Stack<Character>> stacks) {
        List<String> lines = new LinkedList<>();
        String footer = "";
        for (int i = 0; i < stacks.size(); ++i) {
            footer += " " + (i + 1) + "  ";
        }
        lines.add(footer);
        int max = stacks.stream().mapToInt(s -> s.size()).max().getAsInt();
        for (int i = 0; i < max; ++i) {
            String line = "";
            for (List<Character> stack : stacks) {
                if (i < stack.size()) {
                    line += String.format("[%s] ", "" + stack.get(i));
                } else {
                    line += "    ";
                }
            }
            lines.add(0, line);
        }
        lines.forEach(System.out::println);
    }
}