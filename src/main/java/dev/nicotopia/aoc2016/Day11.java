package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day11 {
    private static class State {
        private final List<String> firstFloor = new LinkedList<>();
        private final List<String> secondFloor = new LinkedList<>();
        private final List<String> thirdFloor = new LinkedList<>();
        private final List<String> fourthFloor = new LinkedList<>();

        public List<String> getFloor(int idx) {
            return switch (idx) {
                case 0 -> this.firstFloor;
                case 1 -> this.secondFloor;
                case 2 -> this.thirdFloor;
                case 3 -> this.fourthFloor;
                default -> throw new IllegalArgumentException();
            };
        }

        public int getMinStepCount() {
            return 3 * (2 * (this.firstFloor.size() - 1) - 1) + 4 * this.secondFloor.size()
                    + 2 * this.thirdFloor.size();
        }

        public void print() {
            List<String> objects = IntStream.range(0, 4).mapToObj(this::getFloor).collect(LinkedList::new,
                    LinkedList::addAll, LinkedList::addAll);
            Collections.sort(objects);
            for (int i = 4; i != 0; --i) {
                System.out.print("F" + i);
                System.out.print(i == 1 ? " E " : " . ");
                for (String obj : objects) {
                    System.out.print(this.getFloor(i - 1).contains(obj) ? " " + obj : " . ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private static char next = 'A';

    public static void main(String args[]) throws IOException {
        State initialState = new State();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day11.class.getResourceAsStream("/2016/day11.txt")))) {
            Map<String, Character> knownElements = new HashMap<>();
            Pattern pattern = Pattern.compile("The (\\w+) floor contains (.*)\\.");
            br.lines().map(pattern::matcher).filter(Matcher::matches).forEach(m -> {
                int floorNr = switch (m.group(1)) {
                    case "first" -> 0;
                    case "second" -> 1;
                    case "third" -> 2;
                    case "fourth" -> 3;
                    default -> throw new RuntimeException();
                };
                List<String> floor = initialState.getFloor(floorNr);
                Matcher m2 = Pattern.compile("(\\w+)(-compatible microchip| generator)").matcher(m.group(2));
                while (m2.find()) {
                    String element = m2.group(1);
                    Character c = knownElements.get(element);
                    if (c == null) {
                        char t = Character.toUpperCase(element.charAt(0));
                        knownElements.put(element, c = knownElements.values().contains(t) ? next++ : t);
                    }
                    floor.add((c + (m2.group(2).charAt(0) == '-' ? "m" : "g")).toUpperCase());
                }
                Collections.sort(floor);
            });
        }
        initialState.print();
        long begin = System.nanoTime();
        int minSteps = initialState.getMinStepCount();
        long ns = System.nanoTime() - begin;
        System.out.printf("min steps: %d, elapsed time: %d ns.\n", minSteps, ns);
    }
}