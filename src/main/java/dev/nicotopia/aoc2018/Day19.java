package dev.nicotopia.aoc2018;

import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc2018.Machine.OpCode;
import dev.nicotopia.aoc2018.Machine.Operation;

public class Day19 extends DayBase {
    private Machine m = new Machine(6);
    private int ipReg;
    private List<Operation> ops;

    private void processInput() {
        Pattern p = Pattern.compile("([a-z]+) (\\d+) (\\d+) (\\d+)");
        this.ops = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches)
                .map(m -> new Operation(OpCode.valueOf(m.group(1).toUpperCase()), Integer.valueOf(m.group(2)),
                        Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4))))
                .toList();
        this.ipReg = this.getPrimaryPuzzleInput().stream().filter(l -> l.startsWith("#ip ")).findAny()
                .map(l -> Integer.valueOf(l.substring(4))).get();
    }

    private int partOne() {
        m.execute(this.ops, OptionalInt.of(this.ipReg));
        return m.register(0);
    }

    private int partTwo() {
        m.reset();
        m.register(0, 1);
        Thread t = new Thread(() -> m.execute(ops, OptionalInt.of(this.ipReg)));
        t.start();
        try {
            Thread.sleep(200);
            t.interrupt();
            t.join();
        } catch (InterruptedException ex) {
        }
        int n = IntStream.range(0, 6).map(m::register).max().getAsInt();
        return IntStream.rangeClosed(1, n).filter(i -> n % i == 0).sum();
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2018/day19e.txt");
        this.addTask("Process input", this::processInput);
        this.ops.forEach(o -> System.out.println(o.toCLine()));
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
        this.addTask("Note",
                () -> "Inspecting it by hand I found that my puzzle input sums up all integer dividers\nof a big number. I did not check other puzzle inputs. So it may very well be\nthat the solution to part 2 is not correct for yours. ");
    }
}