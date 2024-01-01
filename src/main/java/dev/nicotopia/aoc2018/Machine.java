package dev.nicotopia.aoc2018;

import java.util.Arrays;
import java.util.List;

public class Machine {
    public enum OpCode {
        ADDR, ADDI, MULR, MULI, BANR, BANI, BORR, BORI, SETR, SETI, GTIR, GTRI, GTRR, EQIR, EQRI, EQRR
    }

    public record Operation(OpCode code, int a, int b, int c) {
    }

    private int registers[] = new int[4];

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
        ops.forEach(this::execute);
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
}
