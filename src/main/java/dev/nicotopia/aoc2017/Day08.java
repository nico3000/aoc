package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Day08 {
    private enum LogicOp {
        EQ, NEQ, LEQ, LE, GEQ, GR
    }

    private record Op(String dstReg, int incAmount, String cmpReg, LogicOp logicOp, int cmpValue) {
        public Op(String dstReg, String type, int amount, String cmpReg, String logicOp, int cmpValue) {
            this(dstReg, switch (type) {
                case "dec" -> -amount;
                case "inc" -> amount;
                default -> throw new RuntimeException();
            }, cmpReg, switch (logicOp) {
                case "==" -> LogicOp.EQ;
                case "!=" -> LogicOp.NEQ;
                case "<=" -> LogicOp.LEQ;
                case "<" -> LogicOp.LE;
                case ">=" -> LogicOp.GEQ;
                case ">" -> LogicOp.GR;
                default -> throw new RuntimeException();
            }, cmpValue);
        }

        public void execute(Map<String, Integer> registers) {
            int cmp = Optional.ofNullable(registers.get(this.cmpReg)).orElse(0);
            if (switch (this.logicOp) {
                case EQ -> cmp == this.cmpValue;
                case NEQ -> cmp != this.cmpValue;
                case LEQ -> cmp <= this.cmpValue;
                case LE -> cmp < this.cmpValue;
                case GEQ -> cmp >= this.cmpValue;
                case GR -> cmp > this.cmpValue;
            }) {
                registers.put(this.dstReg, Optional.ofNullable(registers.get(this.dstReg)).orElse(0) + this.incAmount);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<Op> ops;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day08.class.getResourceAsStream("/2017/day08.txt")))) {
            ops = br.lines().map(l -> l.split("\\s+"))
                    .map(s -> new Op(s[0], s[1], Integer.valueOf(s[2]), s[4], s[5], Integer.valueOf(s[6]))).toList();
        }
        Map<String, Integer> registers = new HashMap<>();
        int max = Integer.MIN_VALUE;
        for (Op op : ops) {
            op.execute(registers);
            max = Math.max(max, registers.values().stream().mapToInt(Integer::valueOf).max().getAsInt());
        }
        int lastMax = registers.values().stream().mapToInt(Integer::valueOf).max().getAsInt();
        System.out.printf("Max value at end: %d, max value of all times: %d\n", lastMax, max);
    }
}
