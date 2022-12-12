package dev.nicotopia.aoc2016;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Machine {
    private enum OpType {
        CPY, INC, DEC, JNZ, TGL, OUT,
    }

    private record Op(OpType type, int arg0, boolean arg0Reg, int arg1, boolean arg1Reg) {
        public int evalArg0(Machine machine) {
            return this.arg0Reg ? machine.registers[this.arg0] : this.arg0;
        }

        public int evalArg1(Machine machine) {
            return this.arg1Reg ? machine.registers[this.arg1] : this.arg1;
        }

        public Op transform(OpType type) {
            return new Op(type, this.arg0, this.arg0Reg, this.arg1, this.arg1Reg);
        }

        public void execute(Machine machine) {
            int idx;
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
                case TGL:
                    idx = machine.ip + this.evalArg0(machine);
                    if (idx < machine.ic.length) {
                        machine.ic[idx] = machine.ic[idx].transform(switch (machine.ic[idx].type) {
                            case CPY -> OpType.JNZ;
                            case INC -> OpType.DEC;
                            case DEC -> OpType.INC;
                            case JNZ -> OpType.CPY;
                            case TGL -> OpType.INC;
                            case OUT -> OpType.OUT;
                        });
                    }
                    ++machine.ip;
                    break;
                case OUT:
                    if (machine.outputHandler != null) {
                        machine.outputHandler.accept(machine, this.evalArg0(machine));
                    }
                    ++machine.ip;
                    break;
            }
        }
    }

    public static Machine parseAssembunny(List<String> lines) {
        Pattern p = Pattern.compile("^([a-z]{3}) ([-0-9]+|[a-d])( ([-0-9]+|[a-d]))?$");
        List<Op> ops = lines.stream().map(p::matcher).filter(Matcher::matches).map(m -> {
            OpType opType = switch (m.group(1)) {
                case "cpy" -> OpType.CPY;
                case "inc" -> OpType.INC;
                case "dec" -> OpType.DEC;
                case "jnz" -> OpType.JNZ;
                case "tgl" -> OpType.TGL;
                case "out" -> OpType.OUT;
                default -> throw new RuntimeException();
            };
            boolean arg0Reg = Character.isLowerCase(m.group(2).charAt(0));
            boolean arg1Reg = m.group(4) != null && Character.isLowerCase(m.group(4).charAt(0));
            int arg0 = arg0Reg ? m.group(2).charAt(0) - 'a' : Integer.valueOf(m.group(2));
            int arg1 = m.group(4) == null ? 0 : arg1Reg ? m.group(4).charAt(0) - 'a' : Integer.valueOf(m.group(4));
            return new Op(opType, arg0, arg0Reg, arg1, arg1Reg);
        }).toList();
        return new Machine(ops);
    }

    private final int registers[] = new int[4];
    private final Op ic[];
    private int ip;
    private boolean interrupted;
    private long execCounter;
    private BiConsumer<Machine, Integer> outputHandler;

    public Machine(List<Op> ops) {
        this.ic = ops.stream().toArray(Op[]::new);
        this.ip = 0;
    }

    public void initialize(int regA, int regB, int regC, int regD) {
        this.registers[0] = regA;
        this.registers[1] = regB;
        this.registers[2] = regC;
        this.registers[3] = regD;
        this.ip = 0;
        this.interrupted = false;
        this.execCounter = 0;
    }

    public void execute() {
        while (this.ip < this.ic.length && !this.interrupted) {
            this.ic[this.ip].execute(this);
            ++this.execCounter;
        }
    }

    public void interrupt() {
        this.interrupted = true;
    }

    public long getExecutionCounter() {
        return this.execCounter;
    }

    public void setOutputHandler(BiConsumer<Machine, Integer> outputHandler) {
        this.outputHandler = outputHandler;
    }

    public int regA() {
        return this.registers[0];
    }

    public int regB() {
        return this.registers[1];
    }

    public int regC() {
        return this.registers[2];
    }

    public int regD() {
        return this.registers[3];
    }
}
