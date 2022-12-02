package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day12 {
    private static enum OpType {
        CPY, INC, DEC, JNZ,
    }

    private static record Op(OpType type, int arg0, boolean arg0Reg, int arg1, boolean arg1Reg) {
        public int evalArg0(Machine machine) {
            return this.arg0Reg ? machine.registers[this.arg0] : this.arg0;
        }

        public int evalArg1(Machine machine) {
            return this.arg1Reg ? machine.registers[this.arg1] : this.arg1;
        }

        public void execute(Machine machine) {
            switch (this.type) {
                case CPY:
                    machine.registers[this.arg1] = this.evalArg0(machine);
                    ++machine.ip;
                    break;
                case INC:
                    ++machine.registers[this.arg0];
                    ++machine.ip;
                    break;
                case DEC:
                    --machine.registers[this.arg0];
                    ++machine.ip;
                    break;
                case JNZ:
                    machine.ip += this.evalArg0(machine) != 0 ? this.evalArg1(machine) : 1;
                    break;
            }
        }
    }

    private static final class Machine {
        private final int registers[] = new int[4];
        private int ip = 0;
        private Op ic[];

        public void initialize(int regA, int regB, int regC, int regD) {
            this.registers[0] = regA;
            this.registers[1] = regB;
            this.registers[2] = regC;
            this.registers[3] = regD;
            this.ip = 0;
        }

        public void execute() {
            while (this.ip < this.ic.length) {
                this.ic[this.ip].execute(this);
            }
        }
    }

    public static void main(String args[]) throws IOException {
        Machine machine = new Machine();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day12.class.getResourceAsStream("/2016/day12.txt")))) {
            Pattern p = Pattern.compile("^([a-z]{3}) ([-0-9]+|[a-d])( ([-0-9]+|[a-d]))?$");
            machine.ic = br.lines().map(p::matcher).filter(Matcher::matches).map(m -> {
                OpType opType = switch (m.group(1)) {
                    case "cpy" -> OpType.CPY;
                    case "inc" -> OpType.INC;
                    case "dec" -> OpType.DEC;
                    case "jnz" -> OpType.JNZ;
                    default -> throw new RuntimeException();
                };
                boolean arg0Reg = Character.isLowerCase(m.group(2).charAt(0));
                boolean arg1Reg = m.group(4) != null && Character.isLowerCase(m.group(4).charAt(0));
                int arg0 = arg0Reg ? m.group(2).charAt(0) - 'a' : Integer.valueOf(m.group(2));
                int arg1 = m.group(4) == null ? 0 : arg1Reg ? m.group(4).charAt(0) - 'a' : Integer.valueOf(m.group(4));
                return new Op(opType, arg0, arg0Reg, arg1, arg1Reg);
            }).toArray(Op[]::new);
        }
        machine.execute();
        int partOne = machine.registers[0];
        machine.initialize(0, 0, 1, 0);;
        machine.execute();
        int partTwo = machine.registers[0];
        System.out.printf("register a; part one: %d, part two: %d", partOne, partTwo);
    }
}