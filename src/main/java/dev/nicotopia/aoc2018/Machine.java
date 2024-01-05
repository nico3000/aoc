package dev.nicotopia.aoc2018;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Machine {
    public enum OpCode {
        ADDR, ADDI, MULR, MULI, BANR, BANI, BORR, BORI, SETR, SETI, GTIR, GTRI, GTRR, EQIR, EQRI, EQRR
    }

    public record Operation(OpCode code, int a, int b, int c) {
        public static Operation createFromLine(String line) {
            String o = Arrays.stream(OpCode.values()).map(c -> c.toString().toLowerCase())
                    .collect(Collectors.joining("|"));
            Matcher m = Pattern.compile(String.format("^(%s)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)$", o))
                    .matcher(line);
            return m.matches() ? new Operation(OpCode.valueOf(m.group(1).toUpperCase()), Integer.valueOf(m.group(2)),
                    Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4))) : null;
        }

        @Override
        public String toString() {
            return String.format("%s %d %d %d", this.code.toString().toLowerCase(), this.a, this.b, this.c);
        }

        public String toCLine() {
            String r = "r" + this.c + " = ";
            r += (this.code == OpCode.SETI || this.code == OpCode.EQIR || this.code == OpCode.GTIR ? "" : "r") + this.a;
            r += switch (this.code) {
                case ADDR, ADDI -> " + ";
                case MULR, MULI -> " * ";
                case BANR, BANI -> " & ";
                case BORR, BORI -> " | ";
                case SETR, SETI -> "";
                case GTIR, GTRI, GTRR -> " > ";
                case EQIR, EQRI, EQRR -> " == ";
            };
            r += switch (this.code) {
                case ADDR, MULR, BANR, BORR, GTIR, GTRR, EQIR, EQRR -> "r" + this.b;
                case ADDI, MULI, BANI, BORI, GTRI, EQRI -> this.b;
                case SETR, SETI -> "";
            };
            if (this.code == OpCode.GTIR || this.code == OpCode.GTRI || this.code == OpCode.GTRR
                    || this.code == OpCode.EQIR || this.code == OpCode.EQRI || this.code == OpCode.EQRR) {
                r += " ? 1 : 0";
            }
            return r + ";";
        }
    }

    public Machine(int numRegisters) {
        this.registers = new int[numRegisters];
    }

    private final int registers[];
    private boolean logging = false;

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public void execute(Operation op) {
        this.registers[op.c] = switch (op.code) {
            case ADDR -> this.registers[op.a] + this.registers[op.b];
            case ADDI -> this.registers[op.a] + op.b;
            case MULR -> this.registers[op.a] * this.registers[op.b];
            case MULI -> this.registers[op.a] * op.b;
            case BANR -> this.registers[op.a] & this.registers[op.b];
            case BANI -> this.registers[op.a] & op.b;
            case BORR -> this.registers[op.a] | this.registers[op.b];
            case BORI -> this.registers[op.a] | op.b;
            case SETR -> this.registers[op.a];
            case SETI -> op.a;
            case GTIR -> this.registers[op.b] < op.a ? 1 : 0;
            case GTRI -> op.b < this.registers[op.a] ? 1 : 0;
            case GTRR -> this.registers[op.b] < this.registers[op.a] ? 1 : 0;
            case EQIR -> op.a == this.registers[op.b] ? 1 : 0;
            case EQRI -> this.registers[op.a] == op.b ? 1 : 0;
            case EQRR -> this.registers[op.a] == this.registers[op.b] ? 1 : 0;
        };
    }

    public void execute(List<Operation> ops) {
        this.execute(ops, OptionalInt.empty());
    }

    public void execute(List<Operation> ops, OptionalInt ipReg) {
        int ip = 0;
        while (0 <= ip && ip < ops.size() && !Thread.currentThread().isInterrupted()) {
            if (ipReg.isPresent()) {
                this.register(ipReg.getAsInt(), ip);
            }
            if (this.logging) {
                System.out.printf("ip=%2d %s", ip, this);
            }
            this.execute(ops.get(ip));
            if (this.logging) {
                System.out.printf(" %s %s\n", ops.get(ip), this);
            }
            if (ipReg.isPresent()) {
                ip = this.register(ipReg.getAsInt());
            }
            ++ip;
        }
    }

    public int register(int r) {
        return this.registers[r];
    }

    public void register(int r, int v) {
        this.registers[r] = v;
    }

    public void reset() {
        Arrays.fill(this.registers, 0);
    }

    @Override
    public String toString() {
        return String.format("[%s]",
                Arrays.stream(this.registers).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
    }
}
