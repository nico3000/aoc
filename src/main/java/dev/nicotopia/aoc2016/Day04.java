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

public class Day04 {
    private static int sum = 0;

    private static record Room(String name, int id, String checksum) {
        private boolean isReal() {
            Map<Character, Integer> map = new HashMap<>();
            for (char c : this.name.replace("-", "").toCharArray()) {
                map.put(c, (map.containsKey(c) ? map.get(c) : 0) + 1);
            }
            List<CharStat> charStats = new LinkedList<>();
            map.forEach((k, v) -> charStats.add(new CharStat(k, v)));
            Collections.sort(charStats);
            for (int i = 0; i < Math.min(5, charStats.size()); ++i) {
                if (charStats.get(i).c != this.checksum.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        public String decodeName() {
            return this.name.chars().collect(StringBuffer::new, (s, c) -> {
                s.append((char) (c == '-' ? c : ('a' + ((c - 'a' + this.id) % 26))));
            }, StringBuffer::append).toString();
        }
    }

    public static void main(String args[]) throws IOException {
        List<Room> rooms;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day04.class.getResourceAsStream("/2016/day04.txt")))) {
            Pattern p = Pattern.compile("^([a-z\\-]+)-([0-9]+)\\[([a-z]{5})\\]$");
            rooms = br.lines().collect(LinkedList<Room>::new, (list, line) -> {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    list.add(new Room(m.group(1), Integer.valueOf(m.group(2)), m.group(3)));
                }
            }, LinkedList<Room>::addAll).stream().filter(Room::isReal).toList();
        }
        rooms.forEach(r -> sum += r.id());
        System.out.printf("real rooms id sum=%d\n", sum);
        rooms.forEach(r -> System.out.printf("%3d: %s\n", r.id(), r.decodeName()));
    }

    private static record CharStat(char c, int count) implements Comparable<CharStat> {
        @Override
        public int compareTo(CharStat right) {
            return this.count == right.count ? Character.compare(this.c, right.c)
                    : -Integer.compare(this.count, right.count);
        }
    }
}
