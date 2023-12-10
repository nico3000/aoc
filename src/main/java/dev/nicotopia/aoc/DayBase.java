package dev.nicotopia.aoc;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme;

import dev.nicotopia.Util;

public abstract class DayBase implements Runnable {
    public static final Font MONOSPACED_FONT = DayBase.getMonospedFont();

    private static Font getMonospedFont() {
        return Arrays
                .stream(new String[] { "DejaVu Sans Mono", "Inconsolata", "Consolas", "Monospaced", "Courier New", })
                .filter(Arrays.asList(
                        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())::contains)
                .findFirst().map(f -> new Font(f, Font.PLAIN, 14)).orElse(null);
    }

    private record TaskResult(String name, String value, long elapsedNanos) {
    }

    private final List<TaskResult> taskResults = new LinkedList<>();
    private PuzzleInputSelector puzzleInputSelector = this.createPuzzleInputSelector();
    private boolean reuseLastInput = false;
    private final Timer timer = new Timer();
    private PuzzleInput puzzleInput;

    public DayBase() {
        FlatGradiantoDeepOceanIJTheme.setup();
    }

    public void registerSecondaryInputs(String... names) {
        this.puzzleInputSelector.registerSecondaryInputs(names);
    }

    public void addPresetFromResource(String presetName, String primaryInputResourcePath,
            Object... secondaryInputValues) {
        this.puzzleInputSelector.addPresetFromResource(presetName, primaryInputResourcePath, secondaryInputValues);
    }

    public void addPreset(String presetName, List<String> primaryInputLines, Object... secondaryInputValues) {
        this.puzzleInputSelector.addPreset(presetName, primaryInputLines, secondaryInputValues);
    }

    public void addPreset(String presetName, Object primaryInput, Object... secondaryInputValues) {
        this.puzzleInputSelector.addPreset(presetName, String.valueOf(primaryInput), secondaryInputValues);
    }

    private PuzzleInput getPuzzleInput() {
        if (this.puzzleInput == null) {
            this.timer.pause();
            try {
                this.puzzleInput = this.puzzleInputSelector.getPuzzleInput(this.reuseLastInput);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
            this.timer.resume();
        }
        if (puzzleInput == null) {
            System.exit(0);
        }
        return this.puzzleInput;
    }

    public List<String> getPrimaryPuzzleInput() {
        return this.getPuzzleInput().getPrimaryInputLines();
    }

    public int getIntInput(String name) {
        return Integer.valueOf(this.getInput(name));
    }

    public String getInput(String name) {
        return this.getPuzzleInput().getSecondaryInput(name);
    }

    public void addTask(String name, Runnable taskFn) {
        this.timer.start();
        try {
            taskFn.run();
            this.taskResults.add(new TaskResult(name, null, timer.stop()));
        } catch (AocException ex) {
            this.taskResults.add(new TaskResult(name, ex.getMessage(), timer.stop()));
        }
    }

    public <E> E addSilentTask(String name, Supplier<E> taskFn) {
        this.timer.start();
        try {
            E result = taskFn.get();
            this.taskResults.add(new TaskResult(name, null, timer.stop()));
            return result;
        } catch (AocException ex) {
            this.taskResults.add(new TaskResult(name, ex.getMessage(), timer.stop()));
            return null;
        }
    }

    public <E> E addTask(String name, Supplier<E> taskFn) {
        this.timer.start();
        try {
            E result = taskFn.get();
            this.taskResults.add(new TaskResult(name, String.valueOf(result), timer.stop()));
            return result;
        } catch (AocException ex) {
            this.taskResults.add(new TaskResult(name, ex.getMessage(), timer.stop()));
            return null;
        }
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
            InfoFrame.showText("Results", resultsBuilder.toString().trim(), MONOSPACED_FONT, "OK");
        }
    }

    private PuzzleInputSelector createPuzzleInputSelector() {
        Matcher m = Pattern.compile("dev\\.nicotopia\\.aoc(\\d+)\\.Day(\\d+)").matcher(this.getClass().getName());
        if (!m.matches()) {
            return new PuzzleInputSelector(0, 0);
        }

        return new PuzzleInputSelector(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
    }

    public static void main(String args[])
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        String className = null;
        var argList = Arrays.asList(args);
        if (!argList.contains("--help") && !argList.contains("-h")) {
            int posYear = argList.indexOf("-y");
            int posDay = argList.indexOf("-d");
            int posClass = argList.indexOf("-c");
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

        System.out.printf("trying to start aoc instance from class: %s\n", className);
        if (ClassLoader.getSystemClassLoader().loadClass(className).getConstructor()
                .newInstance() instanceof DayBase aoc) {
            aoc.reuseLastInput = argList.indexOf("-r") != -1;
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
        System.out.printf("\t-r\t\tIf available, don't ask for input and reuse data from %s.\n",
                PuzzleInputSelector.LAST_INPUT_FILE);
    }
}