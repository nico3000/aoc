package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Day12 {
    public static class JsonObject {
        public enum Type {
            STRING, NUMBER, ARRAY, OBJECT
        }

        private final Type type;
        private final String stringValue;
        private final double numberValue;
        private final JsonObject arrayValue[];
        private final Map<String, JsonObject> objectValue;

        public JsonObject(String value) {
            this.type = Type.STRING;
            this.stringValue = value;
            this.numberValue = Double.NaN;
            this.arrayValue = null;
            this.objectValue = null;
        }

        public JsonObject(double value) {
            this.type = Type.NUMBER;
            this.stringValue = null;
            this.numberValue = value;
            this.arrayValue = null;
            this.objectValue = null;
        }

        public JsonObject(JsonObject value[]) {
            this.type = Type.ARRAY;
            this.stringValue = null;
            this.numberValue = Double.NaN;
            this.arrayValue = value;
            this.objectValue = null;
        }

        public JsonObject(Map<String, JsonObject> value) {
            this.type = Type.OBJECT;
            this.stringValue = null;
            this.numberValue = Double.NaN;
            this.arrayValue = null;
            this.objectValue = value;
        }

        public double sumUpAllNumbers() {
            return switch (this.type) {
                case STRING -> 0.0;
                case NUMBER -> this.numberValue;
                case ARRAY -> Arrays.stream(this.arrayValue).mapToDouble(JsonObject::sumUpAllNumbers).sum();
                case OBJECT -> this.objectValue.values().stream().mapToDouble(JsonObject::sumUpAllNumbers).sum();
            };
        }

        public double sumUpAllNumbersExcept(String except) {
            return switch (this.type) {
                case STRING -> 0.0;
                case NUMBER -> this.numberValue;
                case ARRAY -> Arrays.stream(this.arrayValue).mapToDouble(o -> o.sumUpAllNumbersExcept(except)).sum();
                case OBJECT -> this.objectValue.containsKey(except) || this.objectValue.values().stream()
                        .anyMatch(o -> o.type == Type.STRING && o.stringValue.equals(except)) ? 0.0
                                : this.objectValue.values().stream().mapToDouble(o -> o.sumUpAllNumbersExcept(except))
                                        .sum();
            };
        }
    }

    public static void main(String[] args) throws IOException {
        String input;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day12.class.getResourceAsStream("/2015/day12.txt")))) {
            input = br.readLine();
        }
        List<JsonObject> subObjects = new LinkedList<>();
        Pattern arrayPattern = Pattern.compile("\\[([^\\[\\]\\{\\}]*)\\]");
        Pattern objecPattern = Pattern.compile("\\{([^\\[\\]\\{\\}]*)\\}");
        Pattern stringPattern = Pattern.compile("\"([^\"]*)\""); // "some\"text" wouldn't work
        input = stringPattern.matcher(input).replaceAll(mr -> {
            subObjects.add(new JsonObject(mr.group(1)));
            return "_" + (subObjects.size() - 1);
        });
        for (;;) {
            String newInput = arrayPattern.matcher(input).replaceAll(mr -> {
                subObjects.add(parseArrayContents(mr.group(1), subObjects));
                return "_" + (subObjects.size() - 1);
            });
            newInput = objecPattern.matcher(newInput).replaceAll(mr -> {
                subObjects.add(parseObjectContents(mr.group(1), subObjects));
                return "_" + (subObjects.size() - 1);
            });
            if (input.equals(newInput)) {
                break;
            }
            input = newInput;
        }
        JsonObject root = subObjects.get(Integer.valueOf(input.substring(1)));
        System.out.printf("Part one: %.0f\nPart two: %.0f", root.sumUpAllNumbers(), root.sumUpAllNumbersExcept("red"));
    }

    private static JsonObject parseArrayContents(String json, List<JsonObject> subObjects) {
        return new JsonObject(
                Arrays.stream(json.split(",")).map(s -> getBasicObject(s, subObjects)).toArray(JsonObject[]::new));
    }

    private static JsonObject parseObjectContents(String json, List<JsonObject> subObjects) {
        Map<String, JsonObject> map = new HashMap<>();
        Arrays.stream(json.split(",")).forEach(s -> {
            String split[] = s.split(":", 2);
            JsonObject first = getBasicObject(split[0], subObjects);
            JsonObject second = getBasicObject(split[1], subObjects);
            if (first.type != JsonObject.Type.STRING) {
                throw new RuntimeException("Json object keys must be strings");
            }
            map.put(first.stringValue, second);
        });
        return new JsonObject(map);
    }

    private static JsonObject getBasicObject(String json, List<JsonObject> subObjects) {
        json = json.trim();
        return switch (json.charAt(0)) {
            case '_' -> subObjects.get(Integer.valueOf(json.substring(1)));
            default -> new JsonObject(Double.valueOf(json));
        };
    }
}