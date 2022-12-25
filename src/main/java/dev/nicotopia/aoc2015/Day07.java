package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day07 {
    private static final Map<String, Node> outputWires = new HashMap<>();
    private static final Map<String, Short> knownWires = new HashMap<>();

    public record Arg(String raw) {
        public short get() {
            if (Character.isDigit(this.raw.charAt(0))) {
                return Integer.valueOf(this.raw).shortValue();
            } else {
                Short known = knownWires.get(this.raw);
                if (known == null) {
                    knownWires.put(this.raw, known = outputWires.get(this.raw).execute());
                }
                return known;
            }
        }
    }

    public static class Node {
        private final String type;
        private final Arg inputs[];

        public Node(String type, String inputB, String inputA) {
            this.type = type;
            this.inputs = new Arg[] { new Arg(inputA), new Arg(inputB) };
        }

        public short getInput(int idx) {
            return this.inputs[idx].get();
        }

        public short execute() {
            return switch (this.type) {
                case "NOT" -> (short) ~Short.toUnsignedInt(this.getInput(1));
                case "AND" -> (short) (Short.toUnsignedInt(this.getInput(0)) & Short.toUnsignedInt(this.getInput(1)));
                case "OR" -> (short) (Short.toUnsignedInt(this.getInput(0)) | Short.toUnsignedInt(this.getInput(1)));
                case "LSHIFT" -> (short) (Short.toUnsignedInt(getInput(0)) << this.getInput(1));
                case "RSHIFT" -> (short) (Short.toUnsignedInt(getInput(0)) >> this.getInput(1));
                case "CONST" -> this.getInput(0);
                default -> throw new UnsupportedOperationException();
            };
        }
    }

    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day07.class.getResourceAsStream("/2015/day07.txt")))) {
            Pattern p = Pattern
                    .compile("^((([a-z0-9]+) )?(NOT|AND|OR|LSHIFT|RSHIFT) ([a-z0-9]+)|[a-z0-9]+) -> ([a-z]+)$");
            br.lines().map(p::matcher).filter(Matcher::matches).forEach(m -> {
                outputWires.put(m.group(6), m.group(4) != null ? new Node(m.group(4), m.group(5), m.group(3))
                        : new Node("CONST", null, m.group(1)));
            });
        }
        short a = outputWires.get("a").execute();
        System.out.println("Part one: " + Integer.valueOf(a));
        outputWires.put("b", new Node("CONST", null, Short.toString(a)));
        knownWires.clear();
        System.out.println("Part two: " + Integer.valueOf(outputWires.get("a").execute()));
    }
}