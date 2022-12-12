package dev.nicotopia.aoc2017;

import java.io.IOException;
import java.io.InputStream;

public class Day09 {
    public static void main(String[] args) throws IOException {
        try (InputStream is = Day09.class.getResourceAsStream("/2017/day09.txt")) {
            boolean ignoreNext = false;
            boolean inGarbage = false;
            int level = 0;
            int score = 0;
            int garbageCount = 0;
            int b;
            while ((b = is.read()) != -1) {
                if (ignoreNext) {
                    ignoreNext = false;
                } else if (b == '!') {
                    ignoreNext = true;
                } else {
                    if (inGarbage) {
                        if (b == '>') {
                            inGarbage = false;
                        } else {
                            ++garbageCount;
                        }
                    } else {
                        if (b == '!') {
                            ignoreNext = true;
                        } else if (b == '{') {
                            ++level;
                        } else if (b == '}') {
                            score += level--;
                        } else if (b == '<') {
                            inGarbage = true;
                        }
                    }
                }
            }
            System.out.printf("Score: %d, garbage: %d\n", score, garbageCount);
        }
    }
}
