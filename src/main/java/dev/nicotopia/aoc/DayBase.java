package dev.nicotopia.aoc;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.Util;

public abstract class DayBase implements Runnable {
    private record TaskResult(String name, String value, long elapsedNanos) {
    }

    private final List<TaskResult> taskResults = new LinkedList<>();
    private PuzzleInputSelector puzzleInputSelector = new PuzzleInputSelector(this.getPuzzleName());
    private final Timer timer = new Timer();

    public void registerSecondaryInputs(String... names) {
        this.puzzleInputSelector.registerSecondaryInputs(names);
    }

    public void addPresetFromResource(String presetName, String primaryInputResourcePath,
            Object... secondaryInputNames) {
        this.puzzleInputSelector.addPresetFromResource(presetName, primaryInputResourcePath, secondaryInputNames);
    }

    public List<String> getPrimaryPuzzleInput() {
        return this.puzzleInputSelector.getPuzzleInput(this.timer).getPrimaryInputLines();
    }

    public int getIntInput(String name) {
        return Integer.valueOf(this.getInput(name));
    }

    public String getInput(String name) {
        return this.puzzleInputSelector.getPuzzleInput(this.timer).getSecondaryInput(name);
    }

    public void addTask(String name, Runnable taskFn) {
        this.timer.start();
        taskFn.run();
        this.taskResults.add(new TaskResult(name, null, timer.stop()));
    }

    public <E> E addSilentTask(String name, Supplier<E> taskFn) {
        this.timer.start();
        E result = taskFn.get();
        this.taskResults.add(new TaskResult(name, null, timer.stop()));
        return result;
    }

    public <E> E addTask(String name, Supplier<E> taskFn) {
        this.timer.start();
        E result = taskFn.get();
        this.taskResults.add(new TaskResult(name, String.valueOf(result), timer.stop()));
        return result;
    }

    private void showResults() {
        if (!this.taskResults.isEmpty()) {
            StringBuilder resultsBuilder = new StringBuilder();
            for (TaskResult task : this.taskResults) {
                resultsBuilder.append(String.format("\n%s (%s)", task.name, Util.formatNanos(task.elapsedNanos)));
                if (task.value != null) {
                    String result = String.valueOf(task.value);
                    resultsBuilder.append(String.format(":%c%s", result.contains("\n") ? '\n' : ' ', result));
                }
            }
            ResultFrame.showResults(resultsBuilder.toString().trim(), this.puzzleInputSelector.getMonospacedFont());
        }
    }

    private String getPuzzleName() {
        Matcher m = Pattern.compile("dev\\.nicotopia\\.aoc(\\d+)\\.Day(\\d+)").matcher(this.getClass().getName());
        if (!m.matches()) {
            return this.getClass().getSimpleName();
        }
        return String.format("Advent of Code puzzle %d of year %d", Integer.valueOf(m.group(2)),
                Integer.valueOf(m.group(1)));
    }

    public static void main(String args[])
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        String className = null;
        boolean reuseLastInput = false;
        var argList = Arrays.asList(args);
        if (!argList.contains("--help") && !argList.contains("-h")) {
            int posYear = argList.indexOf("-y");
            int posDay = argList.indexOf("-d");
            int posClass = argList.indexOf("-c");
            reuseLastInput = argList.indexOf("-r") != -1;
            try {
                if (posClass != -1) {
                    className = args[posClass + 1];
                } else if (posDay != -1 && posYear != -1) {
                    int day = Integer.parseInt(args[posDay + 1]);
                    int year = Integer.parseInt(args[posYear + 1]);
                    className = String.format("dev.nicotopia.aoc%04d.Day%02d", year, day);
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            }
        }
        if (className == null) {
            printUsage();
            return;
        }

        System.out.printf("starting aoc instance from: %s\n", className);
        if (ClassLoader.getSystemClassLoader().loadClass(className).getConstructor()
                .newInstance() instanceof DayBase aoc) {
            aoc.puzzleInputSelector.setForceAskForInput(!reuseLastInput);
            aoc.run();
            aoc.showResults();
        } else {
            System.err.printf("given class (%s) does not extend %s\n", className, DayBase.class);
        }
    }

    private static void printUsage() {
        System.out.printf("Usage: %s <-c <fully-qualified-class-name> | -y <year> -d <day>> [-r]\n\n",
                DayBase.class.getSimpleName());
        System.out.println("\t-h, --help\tPrint usage information and exit.");
        System.out.println(
                "\t-c\t\tProvide the fully qualified java class name to load; e.g., dev.nicotopia.aoc2023.Day01.");
        System.out.println("\t-y\t\tProvide the year of the AoC event. If this is set -d must be set too.");
        System.out.println("\t-d\t\tProvide the day of the AoC event. If this is set -y must be set too.");
        System.out.printf("\t-r\t\tIf available, don't ask for input and use data from %s.\n",
                PuzzleInputSelector.LAST_INPUT_FILE);
    }
}