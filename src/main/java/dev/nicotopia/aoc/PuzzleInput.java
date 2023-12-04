package dev.nicotopia.aoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dev.nicotopia.Util;

public class PuzzleInput {
    private final List<String> primaryInput = new ArrayList<>();
    private final Map<String, String> secondaryInputs = new HashMap<>();

    public void setPrimaryInput(String plainText) {
        this.setPrimaryInput(Arrays.asList(plainText.split("\n")));
    }

    public void setPrimaryInput(List<String> lines) {
        this.primaryInput.clear();
        this.primaryInput.addAll(lines);
    }

    public List<String> getPrimaryInputLines() {
        return Collections.unmodifiableList(this.primaryInput);
    }

    public String getPlainPrimaryInput() {
        return this.primaryInput.isEmpty() ? "" : String.join("\n", this.primaryInput);
    }

    public void clearSecondaryInputs() {
        this.secondaryInputs.clear();
    }

    public void setSecondaryInput(String name, String value) {
        this.secondaryInputs.put(name, value);
    }

    public String getSecondaryInput(String name) {
        return this.secondaryInputs.get(name);
    }

    public void copyFrom(PuzzleInput src) {
        this.primaryInput.clear();
        this.secondaryInputs.clear();
        if (src != null) {
            this.primaryInput.addAll(src.primaryInput);
            this.secondaryInputs.putAll(src.secondaryInputs);
        }
    }

    public PuzzleInput copyEmpty() {
        PuzzleInput copy = new PuzzleInput();
        this.secondaryInputs.keySet().forEach(n -> copy.secondaryInputs.put(n, ""));
        return copy;
    }

    public void saveToJsonFile(String path) {
        File file = new File(path);
        try {
            if ((file.exists() || file.createNewFile()) && file.canWrite()) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    JSONObject json = new JSONObject();
                    json.put("primary", new JSONArray(this.primaryInput));
                    json.put("secondary", this.secondaryInputs);
                    bw.write(json.toString(2));
                }
            }
        } catch (IOException ex) {
            System.err.printf("writing to last_input.txt failed: %s\n", ex.getMessage());
        }
    }

    public static PuzzleInput fromJsonFile(String path) {
        File file = new File(path);
        if (file.isFile() && file.canRead()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                JSONObject json = new JSONObject(String.join("\n", br.lines().toList()));
                PuzzleInput input = new PuzzleInput();
                input.primaryInput.addAll(json.optJSONArray("primary", new JSONArray()).toList().stream()
                        .map(o -> o instanceof String str ? str : null).filter(str -> str != null).toList());
                input.secondaryInputs.putAll(Util.pairStream(json.optJSONObject("secondary", new JSONObject()).toMap())
                        .filter(p -> p.second() instanceof String)
                        .collect(Collectors.toMap(p -> p.first(), p -> (String) p.second())));
                return input;
            } catch (JSONException | IOException ex) {
                System.err.printf("Reading %s as JSON file failed: %s\n", path, ex.getMessage());
            }
        }
        return null;
    }
}
