package dev.nicotopia.aoc2019;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.nicotopia.aoc.DayBase;

public class Day07 extends DayBase {
    private IntcodeMachine amplifier;

    private long executeSettingSequencePartOne(int[] settingSequence) {
        long input = 0;
        for (int setting : settingSequence) {
            this.amplifier.reset();
            input = this.amplifier.execute(setting, input).getFirst().longValue();
        }
        return input;
    }

    private void iteratePermutations(List<Integer> l, List<Integer> remaining, Consumer<int[]> onPermutation) {
        if (remaining.size() == 0) {
            onPermutation.accept(l.stream().mapToInt(i -> i).toArray());
        } else {
            for (int i = 0; i < remaining.size(); ++i) {
                l.add(remaining.remove(i));
                this.iteratePermutations(l, remaining, onPermutation);
                remaining.add(i, l.removeLast());
            }
        }
    }

    private Stream<int[]> getPermutations(int... values) {
        List<int[]> sequences = new LinkedList<>();
        this.iteratePermutations(new LinkedList<>(), new LinkedList<>(Arrays.stream(values).boxed().toList()),
                sequences::add);
        return sequences.stream();
    }

    private void processInput() {
        this.amplifier = new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst());
    }

    private long partOne() {
        return this.getPermutations(0, 1, 2, 3, 4).mapToLong(this::executeSettingSequencePartOne).max().getAsLong();
    }

    private class AmpInterface implements Supplier<OptionalLong>, LongConsumer {
        private final Queue<Long> queue = new LinkedList<>();

        public AmpInterface(long... initialOffers) {
            Arrays.stream(initialOffers).forEach(this.queue::add);
        }

        @Override
        public void accept(long value) {
            this.queue.offer(value);
        }

        @Override
        public OptionalLong get() {
            return this.queue.isEmpty() ? OptionalLong.empty() : OptionalLong.of(this.queue.poll());
        }
    }

    private long executeSettingSequencePartTwo(int... settingSequence) {
        AmpInterface[] interfaces = IntStream.range(0, 5).mapToObj(i -> new AmpInterface(settingSequence[i]))
                .toArray(AmpInterface[]::new);
        IntcodeMachine[] amplifiers = IntStream.range(0, 5).mapToObj(i -> this.amplifier.clone())
                .toArray(IntcodeMachine[]::new);
        Arrays.stream(amplifiers).forEach(IntcodeMachine::reset);
        interfaces[0].accept(0);
        boolean allHalted;
        do {
            allHalted = true;
            for (int i = 0; i < 5; ++i) {
                allHalted &= amplifiers[i].execute(interfaces[i],
                        interfaces[(i + 1) % 5]) == IntcodeMachine.Status.HALTED;
            }
        } while (!allHalted);
        return interfaces[0].get().orElse(0);
    }

    private long partTwo() {
        return this.getPermutations(5, 6, 7, 8, 9).mapToLong(this::executeSettingSequencePartTwo).max().getAsLong();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
