package dev.nicotopia.aoc;

public class AocException extends RuntimeException {
    public AocException(String fmt, Object... args) {
        super(String.format(fmt, args));
    }
}
