package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day15 {
    private record Ingredient(String name, int capacity, int durability, int flavor, int texture, int calories) {
    }

    public static void main(String[] args) throws IOException {
        List<Ingredient> ingredients;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day15.class.getResourceAsStream("/2015/day15.txt")))) {
            Pattern p = Pattern.compile(
                    "(\\w+): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)");
            ingredients = br.lines().map(p::matcher).filter(Matcher::matches)
                    .map(m -> new Ingredient(m.group(1), Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3)),
                            Integer.valueOf(m.group(4)), Integer.valueOf(m.group(5)), Integer.valueOf(m.group(6))))
                    .toList();
        }
        LongAccumulator accuPartOne = new LongAccumulator(Math::max, 0);
        LongAccumulator accuPartTwo = new LongAccumulator(Math::max, 0);
        generate(new int[ingredients.size()], 0, 100, d -> {
            long cap = Math.max(0, IntStream.range(0, d.length).map(i -> ingredients.get(i).capacity * d[i]).sum());
            long dur = Math.max(0, IntStream.range(0, d.length).map(i -> ingredients.get(i).durability * d[i]).sum());
            long fla = Math.max(0, IntStream.range(0, d.length).map(i -> ingredients.get(i).flavor * d[i]).sum());
            long tex = Math.max(0, IntStream.range(0, d.length).map(i -> ingredients.get(i).texture * d[i]).sum());
            long score = cap * dur * fla * tex;
            accuPartOne.accumulate(score);
            long cal = IntStream.range(0, d.length).map(i -> ingredients.get(i).calories * d[i]).sum();
            if (cal == 500) {
                accuPartTwo.accumulate(score);
            }
        });
        System.out.printf("Part one: %d\npart two: %d\n", accuPartOne.get(), accuPartTwo.get());
    }

    private static void generate(int distribution[], int pos, int max, Consumer<int[]> consumer) {
        if (pos == distribution.length - 1) {
            distribution[pos] = max;
            consumer.accept(distribution);
        } else {
            for (int i = 0; i <= max; ++i) {
                distribution[pos] = i;
                generate(distribution, pos + 1, max - i, consumer);
            }
        }
    }
}