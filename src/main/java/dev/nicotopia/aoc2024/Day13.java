package dev.nicotopia.aoc2024;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i64;

public class Day13 extends DayBase {
    private record ClawMachine(Vec2i64 a, Vec2i64 b, Vec2i64 prize) {
        public OptionalLong getMinCost() {
            long d = this.a.x() * this.b.y() - this.b.x() * this.a.y();
            long n = this.b.y() * this.prize.x() - this.b.x() * this.prize.y();
            long m = -this.a.y() * this.prize.x() + this.a.x() * this.prize.y();
            return d == 0 || n % d != 0 || m % d != 0 ? OptionalLong.empty() : OptionalLong.of(3 * n / d + m / d);
        }
    }

    private final List<ClawMachine> clawMachines = new ArrayList<>();

    private void buildClawMachines() {
        List<String> inputLines = this.getPrimaryPuzzleInput();
        Pattern btnPattern = Pattern.compile("Button (A|B): X\\+(\\d+), Y\\+(\\d+)");
        Pattern prizePattern = Pattern.compile("Prize: X\\=(\\d+), Y\\=(\\d+)");
        for (int i = 0; i < inputLines.size(); ++i) {
            Matcher btnA = btnPattern.matcher(inputLines.get(i++));
            Matcher btnB = btnPattern.matcher(inputLines.get(i++));
            Matcher prize = prizePattern.matcher(inputLines.get(i++));
            if (btnA.matches() && btnB.matches() && prize.matches()) {
                this.clawMachines.add(new ClawMachine(
                        new Vec2i64(Long.valueOf(btnA.group(2)), Long.valueOf(btnA.group(3))),
                        new Vec2i64(Long.valueOf(btnB.group(2)), Long.valueOf(btnB.group(3))),
                        new Vec2i64(Long.valueOf(prize.group(1)), Long.valueOf(prize.group(2)))));
            } else {
                throw new AocException("malformed input");
            }
        }
    }

    private long partTwo() {
        return this.clawMachines.stream()
                .map(m -> new ClawMachine(m.a, m.b,
                        new Vec2i64(10000000000000L + m.prize.x(), 10000000000000L + m.prize.y())))
                .map(ClawMachine::getMinCost).mapToLong(c -> c.orElse(0)).sum();
    }

    private long partOne() {
        return this.clawMachines.stream().map(ClawMachine::getMinCost).mapToLong(c -> c.orElse(0)).sum();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::buildClawMachines);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
