package dev.nicotopia.aoc2019;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;

public class Day16 extends DayBase {
    private static final int NUM_STEPS = 100;

    private int[] getSignalFromInput() {
        return this.getPrimaryPuzzleInput().getFirst().chars().map(c -> c - '0').toArray();
    }

    private String partOne(int[] signal, int resultOffset) {
        for (int i = 0; i < NUM_STEPS; ++i) {
            long beg = System.nanoTime();
            signal = this.fft(signal);
            long elapsedNanos = System.nanoTime() - beg;
            System.out.printf("Step %d / %d done. (%s)\n", i + 1, NUM_STEPS, Util.formatNanos(elapsedNanos));
        }
        int[] finalResult = signal;
        return IntStream.range(0, 8).mapToObj(i -> "" + finalResult[resultOffset + i]).collect(Collectors.joining());
    }

    private int[] fft(int[] src) {
        int blockLen = 65000;
        int numBlocks = (src.length + blockLen - 1) / blockLen;
        int[] result = new int[src.length];
        for (int b = 0; b < numBlocks; ++b) {
            int start = b * blockLen;
            int end = Math.min(start + blockLen, src.length);
            long beg = System.nanoTime();
            int[] block = IntStream.range(start, end).parallel().map(i -> this.fftValue(src, i)).toArray();
            System.arraycopy(block, 0, result, start, end - start);
            long elapsedNanos = System.nanoTime() - beg;
            if (1 < numBlocks) {
                System.out.printf("Block %d / %d done. (%s)\n", b + 1, numBlocks, Util.formatNanos(elapsedNanos));
            }
        }
        return result;
    }

    private int fftValue(int[] src, int pos) {
        int i = pos;
        int sum = 0;
        int f = 1;
        while (i < src.length) {
            int partialSum = 0;
            for (int j = 0; j <= pos && i < src.length; ++j, ++i) {
                partialSum += src[i];
            }
            sum += f * partialSum;
            f = -f;
            i += pos + 1;
        }
        return (sum < 0 ? -sum : sum) % 10;
    }

    @Override
    public void run() {
        int[] signal = this.addSilentTask("Parse input", this::getSignalFromInput);
        this.addTask("Part one", () -> this.partOne(signal, 0));
        if (Dialog.showYesNoWarning("Warning",
                "I was too lazy to think of a smart algorithm for part two. So, you may run the\n" +
                        "brute force implementation of part one which will result in many hours of\n"
                        + "runtime on 2024 CPUs. However, if you want to reduce that to just minutes AND\n"
                        + "you have an NVIDIA GPU, you may want to give my CUDA implementation a try. You\n"
                        + "can find that at https://github.com/nico3000/aoc-2019-16. Do you want to run\n"
                        + "the slow CPU version now anyway?")) {
            int[] extendedSignal = IntStream.range(0, 10000 * signal.length).map(i -> signal[i % signal.length])
                    .toArray();
            int offset = Integer
                    .valueOf(IntStream.range(0, 7).mapToObj(i -> "" + signal[i]).collect(Collectors.joining()));
            this.addTask("Part two", () -> this.partOne(extendedSignal, offset));
        }
    }
}
