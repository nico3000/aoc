package dev.nicotopia.aoc2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import dev.nicotopia.aoc.AocException;

public class IntcodeMachine {
    public enum Status {
        HALTED, AWAITING_INPUT,
    }

    private final Map<Long, Long> originalProgram;
    private final Map<Long, Long> changed = new HashMap<>();
    private long relativeBase;
    private long ip;

    private IntcodeMachine(Map<Long, Long> originalProgram) {
        this.originalProgram = originalProgram;
    }

    public IntcodeMachine(String values) {
        this.originalProgram = new HashMap<>();
        long[] initial = Arrays.stream(values.split(",")).mapToLong(Long::valueOf).toArray();
        for (int i = 0; i < initial.length; ++i) {
            this.originalProgram.put((long) i, initial[i]);
        }
        this.relativeBase = 0;
        this.ip = 0;
    }

    public void reset() {
        this.changed.clear();
        this.relativeBase = 0L;
        this.ip = 0;
    }

    public void set(long idx, long val) {
        Long originalVal = this.originalProgram.get(idx);
        if ((originalVal != null && originalVal == val) || (originalVal == null && val == 0)) {
            this.changed.remove(idx);
        } else {
            this.changed.put(idx, val);
        }
    }

    public long get(long idx) {
        Long val = this.changed.get(idx);
        return val != null ? val : this.originalProgram.getOrDefault(idx, 0L);
    }

    private long evalParam(int paramIdx) {
        return this.get(this.evalParamToIdx(paramIdx));
    }

    private void writeToParam(int paramIdx, long val) {
        this.set(this.evalParamToIdx(paramIdx), val);
    }

    private long evalParamToIdx(int paramIdx) {
        long pm = (this.get(this.ip) / switch (paramIdx) {
            case 1 -> 100;
            case 2 -> 1000;
            case 3 -> 10000;
            default -> throw new AocException("Illegal parameter index: " + paramIdx);
        }) % 10;
        return switch ((int) pm) {
            case 0 -> this.get(this.ip + paramIdx);
            case 1 -> this.ip + paramIdx;
            case 2 -> this.relativeBase + this.get(this.ip + paramIdx);
            default -> throw new AocException("Illegal parameter mode: " + pm);
        };
    }

    public void execute(LongSupplier input, LongConsumer output) {
        this.execute(() -> OptionalLong.of(input.getAsLong()), output);
    }

    public Status execute(Supplier<OptionalLong> input, LongConsumer output) {
        while (this.get(this.ip) != 99) {
            switch ((int) (this.get(this.ip) % 100)) {
                case 1:
                    this.writeToParam(3, this.evalParam(1) + this.evalParam(2));
                    this.ip += 4;
                    break;
                case 2:
                    this.writeToParam(3, this.evalParam(1) * this.evalParam(2));
                    this.ip += 4;
                    break;
                case 3:
                    OptionalLong v = input.get();
                    if (v.isEmpty()) {
                        return Status.AWAITING_INPUT;
                    }
                    this.writeToParam(1, v.getAsLong());
                    this.ip += 2;
                    break;
                case 4:
                    output.accept(this.evalParam(1));
                    this.ip += 2;
                    break;
                case 5:
                    this.ip = this.evalParam(1) != 0 ? this.evalParam(2) : this.ip + 3;
                    break;
                case 6:
                    this.ip = this.evalParam(1) == 0 ? this.evalParam(2) : this.ip + 3;
                    break;
                case 7:
                    this.writeToParam(3, this.evalParam(1) < this.evalParam(2) ? 1L : 0L);
                    this.ip += 4;
                    break;
                case 8:
                    this.writeToParam(3, this.evalParam(1) == this.evalParam(2) ? 1L : 0L);
                    this.ip += 4;
                    break;
                case 9:
                    this.relativeBase += this.evalParam(1);
                    this.ip += 2;
                    break;
                default:
                    throw new AocException("Illegal opcode: " + this.get(this.ip));
            }
        }
        return Status.HALTED;
    }

    public List<Long> execute(long... inputs) {
        List<Long> output = new ArrayList<>();
        this.execute(new Supplier<OptionalLong>() {
            private int idx = 0;

            @Override
            public OptionalLong get() {
                return this.idx < inputs.length ? OptionalLong.of(inputs[this.idx++]) : OptionalLong.empty();
            }
        }, output::add);
        return output;
    }

    @Override
    public IntcodeMachine clone() {
        IntcodeMachine clone = new IntcodeMachine(this.originalProgram);
        clone.changed.putAll(this.changed);
        clone.relativeBase = this.relativeBase;
        clone.ip = this.ip;
        return clone;
    }
}