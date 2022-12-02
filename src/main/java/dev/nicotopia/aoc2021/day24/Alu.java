package dev.nicotopia.aoc2021.day24;

import java.util.ArrayList;
import java.util.List;

public class Alu {
    public enum Register {
        W, X, Y, Z,
    }

    private class Value {
        private enum Type {
            CONSTANT, REGISTER,
        }

        private final Type type;
        private final int value;

        public Value(String raw) {
            switch (raw) {
                case "w":
                    this.type = Type.REGISTER;
                    this.value = 0;
                    break;
                case "x":
                    this.type = Type.REGISTER;
                    this.value = 1;
                    break;
                case "y":
                    this.type = Type.REGISTER;
                    this.value = 2;
                    break;
                case "z":
                    this.type = Type.REGISTER;
                    this.value = 3;
                    break;
                default:
                    this.type = Type.CONSTANT;
                    this.value = Integer.valueOf(raw);
                    break;
            }
        }

        public int get() {
            return switch (this.type) {
                case CONSTANT -> this.value;
                case REGISTER -> Alu.this.get(this.getRegister());
            };
        }

        public Register getRegister() {
            if (this.type != Type.REGISTER) {
                throw new UnsupportedOperationException("Value is not a register.");
            }
            return switch (this.value) {
                case 0 -> Register.W;
                case 1 -> Register.X;
                case 2 -> Register.Y;
                case 3 -> Register.Z;
                default -> null;
            };
        }
    }

    private enum CommandType {
        INP, ADD, MUL, DIV, MOD, EQL,
    }

    private record Command(CommandType type, Value first, Value second) {
    }

    private class State {
        private int w, x, y, z;
        private int ptr;

        public State(int w, int x, int y, int z, int ptr) {
            this.w = w;
            this.x = x;
            this.y = y;
            this.z = z;
            this.ptr = ptr;
        }

        public State copy() {
            return new State(this.w, this.x, this.y, this.z, this.ptr);
        }

        public void setFrom(State other) {
            this.w = other.w;
            this.x = other.x;
            this.y = other.y;
            this.z = other.z;
            this.ptr = other.ptr;
        }
    }

    private final List<Command> program = new ArrayList<>();
    private final List<Integer> input = new ArrayList<>();
    private final List<State> states = new ArrayList<>();
    private int nextInputPtr = 0;

    public void reset(int... inputValues) {
        this.input.clear();
        for (int i : inputValues) {
            this.input.add(i);
        }
        this.states.clear();
        this.states.add(new State(0, 0, 0, 0, 0));
        this.nextInputPtr = 0;
    }

    public void setInput(int pos, int value) {
        this.input.set(pos, value);
        this.nextInputPtr = Math.min(this.nextInputPtr, pos);
    }

    public int getInput(int pos) {
        return this.input.get(pos);
    }

    public int getInputCount() {
        return this.input.size();
    }

    public void setProgram(String lines[]) {
        for (String line : lines) {
            String split[] = line.split("\\s+");
            this.program.add(switch (split[0]) {
                case "inp" -> new Command(CommandType.INP, new Value(split[1]), null);
                case "add" -> new Command(CommandType.ADD, new Value(split[1]), new Value(split[2]));
                case "mul" -> new Command(CommandType.MUL, new Value(split[1]), new Value(split[2]));
                case "div" -> new Command(CommandType.DIV, new Value(split[1]), new Value(split[2]));
                case "mod" -> new Command(CommandType.MOD, new Value(split[1]), new Value(split[2]));
                case "eql" -> new Command(CommandType.EQL, new Value(split[1]), new Value(split[2]));
                default -> throw new IllegalArgumentException("Unknown command: " + line);
            });
        }
    }

    public void execute() {
        State state = this.states.get(this.nextInputPtr);
        while (state.ptr < this.program.size()) {
            Command cmd = this.program.get(state.ptr);
            if (cmd.type == CommandType.INP) {
                if (this.nextInputPtr + 1 == this.states.size()) {
                    this.states.add(state.copy());
                } else {
                    this.states.get(this.nextInputPtr + 1).setFrom(state);
                }
                state = this.states.get(this.nextInputPtr + 1);
                this.print();
            }
            this.set(cmd.first.getRegister(), switch (cmd.type) {
                case INP -> this.input.get(this.nextInputPtr++);
                case ADD -> cmd.first.get() + cmd.second.get();
                case MUL -> cmd.first.get() * cmd.second.get();
                case DIV -> cmd.first.get() / cmd.second.get();
                case MOD -> cmd.first.get() % cmd.second.get();
                case EQL -> cmd.first.get() == cmd.second.get() ? 1 : 0;
            });
            ++state.ptr;
        }
    }

    public int get(Register r) {
        return switch (r) {
            case W -> this.states.get(this.nextInputPtr).w;
            case X -> this.states.get(this.nextInputPtr).x;
            case Y -> this.states.get(this.nextInputPtr).y;
            case Z -> this.states.get(this.nextInputPtr).z;
        };
    }

    public void set(Register r, int v) {
        switch (r) {
            case W:
                this.states.get(this.nextInputPtr).w = v;
                break;
            case X:
                this.states.get(this.nextInputPtr).x = v;
                break;
            case Y:
                this.states.get(this.nextInputPtr).y = v;
                break;
            case Z:
                this.states.get(this.nextInputPtr).z = v;
                break;
        }
    }

    public void print() {
        for (int i : this.input) {
            System.out.printf("%d", i);
        }
        System.out.printf(": w=%2d x=%2d y=%2d z=%d\n", this.states
                .get(this.nextInputPtr).w, this.states.get(this.nextInputPtr).x,
                this.states.get(this.nextInputPtr).y, this.states.get(this.nextInputPtr).z);
        for (int i = 0; i < this.nextInputPtr; ++i) {
            System.out.print(" ");
        }
        System.out.println("^");
    }
}
