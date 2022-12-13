package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day25 {
    public record Action(int value, int move, char state) {
    }

    public record State(Action zeroAction, Action oneAction) {
    }

    public static void main(String[] args) throws IOException {
        Map<Character, State> states = new HashMap<>();
        char currentState;
        int steps;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day25.class.getResourceAsStream("/2017/day25.txt")))) {
            currentState = br.readLine().charAt("Begin in state ".length());
            String line = br.readLine();
            steps = Integer.valueOf(line.substring("Perform a diagnostic checksum after ".length(),
                    line.length() - " steps.".length()));
            while ((line = br.readLine()) != null) {
                char stateName = br.readLine().charAt("In state ".length());
                br.readLine();
                Action zeroAction = new Action(br.readLine().charAt("    - Write the value ".length()) - '0',
                        br.readLine().charAt("    - Move one slot to the ".length()) == 'r' ? 1 : -1,
                        br.readLine().charAt("    - Continue with state ".length()));
                br.readLine();
                Action oneAction = new Action(br.readLine().charAt("    - Write the value ".length()) - '0',
                        br.readLine().charAt("    - Move one slot to the ".length()) == 'r' ? 1 : -1,
                        br.readLine().charAt("    - Continue with state ".length()));
                states.put(stateName, new State(zeroAction, oneAction));
            }
        }
        Set<Integer> tape = new HashSet<>();
        int currentTapePos = 0;
        for (int i = 0; i < steps; ++i) {
            State state = states.get(currentState);
            Action a = tape.contains(currentTapePos) ? state.oneAction : state.zeroAction;
            if (a.value == 0) {
                tape.remove(currentTapePos);
            } else {
                tape.add(currentTapePos);
            }
            currentTapePos += a.move;
            currentState = a.state;
        }
        System.out.println("Part one: " + tape.size());
    }
}
