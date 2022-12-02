package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day07 {
    private static class IPv7 {
        private final List<String> tokens;

        public IPv7(String raw) {
            this.tokens = Arrays.asList(raw.split("[\\[\\]]"));
        }

        private Stream<String> find(String regex, boolean inSupernetElseInHypernet) {
            Pattern p = Pattern.compile(regex);
            List<String> result = new LinkedList<>();
            for (int i = inSupernetElseInHypernet ? 0 : 1; i < this.tokens.size(); i += 2) {
                Matcher m = p.matcher(this.tokens.get(i));
                int pos = 0;
                while (m.find(pos)) {
                    result.add(m.group(0));
                    pos = m.start() + 1;
                }
            }
            return result.stream();
        }

        public boolean isTlsSupported() {
            return this.find("(\\w)(\\w)\\2\\1", false).allMatch(s -> s.charAt(0) == s.charAt(1))
                    && this.find("(\\w)(\\w)\\2\\1", true).anyMatch(s -> s.charAt(0) != s.charAt(1));
        }

        public boolean isSslSupported() {
            return this.find("(\\w)(\\w)\\1", true).anyMatch(
                    s -> s.charAt(0) != s.charAt(1) && this.find(s.substring(1) + s.charAt(1), false).count() != 0);
        }
    }

    public static void main(String args[]) throws IOException {
        List<IPv7> addresses;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day07.class.getResourceAsStream("/2016/day07.txt")))) {
            addresses = br.lines().collect(LinkedList::new, (list, line) -> list.add(new IPv7(line)),
                    LinkedList::addAll);
        }
        long tlsCount = addresses.stream().filter(IPv7::isTlsSupported).count();
        long sslCount = addresses.stream().filter(IPv7::isSslSupported).count();
        System.out.printf("TLS count: %d, SSL count: %d\n", tlsCount, sslCount);
    }
}