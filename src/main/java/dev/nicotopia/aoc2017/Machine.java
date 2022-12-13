package dev.nicotopia.aoc2017;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class Machine {
    public enum ArgType {
        REG, VAL,
    }

    public record Arg(ArgType type, long val) {
        public Arg(String src) {
            this(src.length() == 1 && 'a' <= src.charAt(0) && src.charAt(0) <= 'z' ? ArgType.REG : ArgType.VAL,
                    src.length() == 1 && 'a' <= src.charAt(0) && src.charAt(0) <= 'z' ? src.charAt(0)
                            : Long.valueOf(src));
        }

        public long eval(Machine m) {
            return switch (type) {
                case VAL -> this.val;
                case REG -> m.getRegister((char) this.val);
            };
        }
    }

    public record Op(String cmd, Arg arg0, Arg arg1) {
        public void execute(Machine m) {
            switch (this.cmd) {
                case "snd" -> m.snd((int) this.arg0.eval(m));
                case "set" -> m.setRegister((char) this.arg0.val, this.arg1.eval(m));
                case "add" -> m.setRegister((char) this.arg0.val, this.arg0.eval(m) + this.arg1.eval(m));
                case "sub" -> m.setRegister((char) this.arg0.val, this.arg0.eval(m) - this.arg1.eval(m));
                case "mul" -> m.setRegister((char) this.arg0.val, this.arg0.eval(m) * this.arg1.eval(m));
                case "mod" -> m.setRegister((char) this.arg0.val, this.arg0.eval(m) % this.arg1.eval(m));
                case "rcv" -> {
                    if (this.arg0.eval(m) != 0 && m.lastSndFrequency != null) {
                        System.out.println("Part one: " + m.lastSndFrequency);
                        m.lastSndFrequency = null;
                    }
                    m.setRegister((char) this.arg0.val, m.rcv());
                }
                case "jgz" -> {
                    if (0 < this.arg0.eval(m)) {
                        m.jmp((int) this.arg1.eval(m));
                    }
                }
                case "jnz" -> {
                    if (this.arg0.eval(m) != 0) {
                        m.jmp((int) this.arg1.eval(m));
                    }
                }
                default -> throw new RuntimeException();
            }
        }
    }

    private final Map<Character, Long> registers = new HashMap<>();
    private final List<Op> program = new LinkedList<>();
    private int ic;
    private final LongConsumer sndConsumer;
    private final LongSupplier rcvSupplier;
    private Long lastSndFrequency = 0L;

    public Machine(int id, List<String> assemblerLines, LongConsumer sndConsumer, LongSupplier rcvSupplier) {
        this.program.addAll(assemblerLines.stream().map(l -> l.split("\\s+"))
                .map(s -> new Op(s[0], new Arg(s[1]), s.length == 2 ? null : new Arg(s[2]))).toList());
        this.sndConsumer = sndConsumer;
        this.rcvSupplier = rcvSupplier;
        this.registers.put('p', (long) id);
    }

    public void zeroRegisters() {
        this.registers.clear();
    }

    public void setRegister(char reg, long value) {
        if (value == 0) {
            this.registers.remove(reg);
        } else {
            this.registers.put(reg, value);
        }
    }

    public long getRegister(char register) {
        return this.registers.getOrDefault(register, 0L);
    }

    public void execute(Consumer<Op> onExecuted) {
        this.ic = 0;
        while (this.ic < this.program.size()) {
            Op op = this.program.get(this.ic++);
            op.execute(this);
            if (onExecuted != null) {
                onExecuted.accept(op);
            }
        }
    }

    public void execute() {
        this.execute(null);
    }

    public void snd(long value) {
        if (this.lastSndFrequency != null) {
            this.lastSndFrequency = value;
        }
        this.sndConsumer.accept(value);
    }

    public long rcv() {
        return this.rcvSupplier.getAsLong();
    }

    public void jmp(int offset) {
        this.ic += offset - 1;
    }
}