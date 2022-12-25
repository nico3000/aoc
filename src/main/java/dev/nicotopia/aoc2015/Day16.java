package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day16 {
    private record PropValuePair(String name, Integer a, Integer b) {
        public boolean mayMatch() {
            return this.a == null || this.b == null || this.a.equals(this.b);
        }

        public boolean mayMatchPartTwo() {
            return switch (this.name) {
                case "cats", "trees" -> this.a == null || this.b < this.a;
                case "pomeranians", "goldfish" -> this.a == null || this.a < this.b;
                default -> this.a == null || this.b == null || this.a.equals(this.b);
            };
        }
    }

    private record Sue(int id, Map<String, Integer> props) {
        public boolean mayMatch(Sue other) {
            return this.props.keySet().stream()
                    .map(key -> new PropValuePair(key, this.props.get(key), other.props.get(key)))
                    .filter(p -> !p.mayMatch()).findAny().isEmpty();
        }

        public boolean mayMatchPartTwo(Sue other) {
            return this.props.keySet().stream()
                    .map(key -> new PropValuePair(key, this.props.get(key), other.props.get(key)))
                    .filter(p -> !p.mayMatchPartTwo()).findAny().isEmpty();
        }
    }

    public static void main(String[] args) throws IOException {
        List<Sue> sues;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day16.class.getResourceAsStream("/2015/day16.txt")))) {
            Pattern p = Pattern.compile("Sue (\\d+): (.*)");
            sues = br.lines().map(p::matcher).filter(Matcher::matches).map(m -> {
                Map<String, Integer> props = new HashMap<>();
                for (String prop : m.group(2).split(", ")) {
                    int pos = prop.indexOf(':');
                    props.put(prop.substring(0, pos), Integer.valueOf(prop.substring(pos + 2)));
                }
                return new Sue(Integer.valueOf(m.group(1)), props);
            }).toList();
        }
        Sue sue = new Sue(-1, new HashMap<>());
        sue.props.put("children", 3);
        sue.props.put("cats", 7);
        sue.props.put("samoyeds", 2);
        sue.props.put("pomeranians", 3);
        sue.props.put("akitas", 0);
        sue.props.put("vizslas", 0);
        sue.props.put("goldfish", 5);
        sue.props.put("trees", 3);
        sue.props.put("cars", 2);
        sue.props.put("perfumes", 1);
        System.out.println("Part one: " + sues.stream().filter(s -> s.mayMatch(sue)).findAny().get().id);
        System.out.println("Part two: " + sues.stream().filter(s -> s.mayMatchPartTwo(sue)).findAny().get().id);
    }
}