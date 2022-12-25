package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day23 {
    private enum OpType {
        HLF, TPL, INC, JMP, JIE, JIO
    }

    private record Op(OpType type, char register, int offset) {
        public void execute(Machine m) {
            switch (this.type) {
                case HLF -> m.registers.put(this.register, m.registers.getOrDefault(this.register, 0) / 2);
                case TPL -> m.registers.put(this.register, m.registers.getOrDefault(this.register, 0) * 3);
                case INC -> m.registers.put(this.register, m.registers.getOrDefault(this.register, 0) + 1);
                case JMP -> m.ip += this.offset;
                case JIE -> m.ip += m.registers.getOrDefault(this.register, 0) % 2 == 0 ? this.offset : 0;
                case JIO -> m.ip += m.registers.getOrDefault(this.register, 0) == 1 ? this.offset : 0;
            }
        }
    }

    private static class Machine {
        private final Map<Character, Integer> registers = new HashMap<>();
        private final List<Op> ic;
        private int ip = 0;

        public Machine(List<Op> ic) {
            this.ic = new ArrayList<>(ic);
        }

        public void execute() {
            while (0 <= this.ip && this.ip < this.ic.size()) {
                int ipBefore = this.ip;
                this.ic.get(this.ip).execute(this);
                if (this.ip == ipBefore) {
                    ++this.ip;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Machine m;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day23.class.getResourceAsStream("/2015/day23.txt")))) {
            m = new Machine(br.lines().map(l -> l.split(", | ")).map(s -> switch (s[0]) {
                case "hlf" -> new Op(OpType.HLF, s[1].charAt(0), 0);
                case "tpl" -> new Op(OpType.TPL, s[1].charAt(0), 0);
                case "inc" -> new Op(OpType.INC, s[1].charAt(0), 0);
                case "jmp" -> new Op(OpType.JMP, (char) 0, Integer.valueOf(s[1]));
                case "jie" -> new Op(OpType.JIE, s[1].charAt(0), Integer.valueOf(s[2]));
                case "jio" -> new Op(OpType.JIO, s[1].charAt(0), Integer.valueOf(s[2]));
                default -> throw new RuntimeException();
            }).toList());
        }
        m.execute();
        System.out.println("Part one: " + m.registers.getOrDefault('b', 0));
        m.ip = 0;
        m.registers.clear();
        m.registers.put('a', 1);
        m.execute();
        System.out.println("Part two: " + m.registers.getOrDefault('b', 0));
    }
}