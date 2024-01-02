package dev.nicotopia.aoc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PuzzleInputSelector {
    public static final String LAST_INPUT_FILE = "last_input.json";

    public record SecondaryInput(String name, Object defaultValue) {
    }

    private final int year;
    private final int day;
    private final Map<String, PuzzleInput> puzzleInputPresets = new LinkedHashMap<>();
    private List<SecondaryInput> secondaryInputs = new LinkedList<>();
    private PuzzleInput puzzleInput = new PuzzleInput();
    private final JTextArea inputTextArea = new JTextArea();
    private final List<JTextField> secondaryInputTextFields = new ArrayList<>();

    public PuzzleInputSelector(int year, int day) {
        this.year = year;
        this.day = day;
        this.inputTextArea.setFont(DayBase.MONOSPACED_FONT);
    }

    public void pushSecondaryInputs(SecondaryInput... inputs) {
        this.secondaryInputs.addAll(Arrays.asList(inputs));
    }

    public void addPreset(String presetName, List<String> primaryInputLines, Object... secondaryInputValues) {
        PuzzleInput preset = this.puzzleInput.copyEmpty();
        preset.setPrimaryInput(primaryInputLines);
        for (int i = 0; i < this.secondaryInputs.size(); ++i) {
            preset.setSecondaryInput(this.secondaryInputs.get(i).name, secondaryInputValues[i].toString());
        }
        this.puzzleInputPresets.put(presetName, preset);
    }

    public void addPreset(String presetName, String rawPrimaryInput, Object... secondaryInputValues) {
        this.addPreset(presetName, Arrays.asList(rawPrimaryInput.split("\n")), secondaryInputValues);
    }

    public void addPresetFromResource(String presetName, String primaryInputResourcePath,
            Object secondaryInputValues[]) {
        if (this.secondaryInputs.size() != secondaryInputValues.length) {
            throw new RuntimeException(String.format(
                    "Number of registered secondary inputs (%d) does not match number of given secondary inputs (%d) for preset %s.\n",
                    this.secondaryInputs.size(), secondaryInputValues.length, presetName));
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                PuzzleInputSelector.class.getResourceAsStream(primaryInputResourcePath)))) {
            this.addPreset(presetName, br.lines().toList(), secondaryInputValues);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public PuzzleInput getPuzzleInput(boolean reuseLastInput) throws InterruptedException {
        PuzzleInput lastInput = PuzzleInput.fromJsonFile(LAST_INPUT_FILE);
        if (lastInput != null
                && this.secondaryInputs.stream().allMatch(si -> lastInput.getSecondaryInput(si.name) != null)) {
            this.puzzleInput.copyFrom(lastInput);
            if (reuseLastInput) {
                return this.puzzleInput;
            }
            this.puzzleInputPresets.put(LAST_INPUT_FILE, lastInput);
        }

        FramePresenter presenter = new FramePresenter(String.format("Advent of Code %d | Day %d", this.year, this.day));

        this.inputTextArea.setText(this.puzzleInput.getPlainPrimaryInput());
        JScrollPane inputScrollPane = new JScrollPane(this.inputTextArea);
        inputScrollPane.setPreferredSize(new Dimension(600, 600));
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("Main puzzle input"));

        JButton ownPuzzleInputButton = new JButton("Own puzzle input");
        ownPuzzleInputButton.addActionListener(evt -> {
            String session = JOptionPane.showInputDialog(presenter.getFrame(), "Please provide your session cookie.",
                    Preferences.userNodeForPackage(this.getClass()).get("session", ""));
            if (session != null) {
                URI uri = URI.create(String.format("https://adventofcode.com/%d/day/%d/input", this.year, this.day));
                Builder builder = HttpRequest.newBuilder(uri).GET().header("Cookie", "session=" + session);
                try {
                    var response = HttpClient.newHttpClient().send(builder.build(), BodyHandlers.ofString());
                    PuzzleInput p = new PuzzleInput();
                    p.setPrimaryInput(response.body());
                    for (SecondaryInput si : this.secondaryInputs) {
                        p.setSecondaryInput(si.name, si.defaultValue == null ? "" : si.defaultValue.toString());
                    }
                    this.apply(p);
                    Preferences.userNodeForPackage(this.getClass()).put("session", session);
                } catch (IOException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(
                            presenter.getFrame(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton fromFileButton = new JButton("File source...");
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fromFileButton.addActionListener(evt -> {
            if (jfc.showOpenDialog(presenter.getFrame()) == JFileChooser.APPROVE_OPTION) {
                this.inputTextArea.setText(this.getFileContents(jfc.getSelectedFile()));
            }
        });

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JPanel presetButtonPanel = new JPanel();
        presetButtonPanel.setLayout(new BoxLayout(presetButtonPanel, BoxLayout.Y_AXIS));
        presetButtonPanel.setBorder(BorderFactory.createTitledBorder("Use data from"));
        presetButtonPanel.add(ownPuzzleInputButton);
        presetButtonPanel.add(fromFileButton);
        for (String presetName : this.puzzleInputPresets.keySet()) {
            JButton btn = new JButton(String.format(presetName));
            btn.addActionListener(evt -> this.apply(this.puzzleInputPresets.get(presetName)));
            presetButtonPanel.add(btn);
        }
        JPanel presetButtonPanelPanel = new JPanel(); // don't ask
        presetButtonPanelPanel.add(presetButtonPanel);
        leftPanel.add(presetButtonPanelPanel);

        if (!this.secondaryInputs.isEmpty()) {
            JPanel additionalInputPanel = new JPanel(new GridLayout(this.secondaryInputs.size(), 2));
            for (SecondaryInput si : this.secondaryInputs) {
                JTextField inputTextField = new JTextField(this.puzzleInput.getSecondaryInput(si.name));
                inputTextField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent evt) {
                        PuzzleInputSelector.this.puzzleInput.setSecondaryInput(si.name, inputTextField.getText());
                    }
                });
                additionalInputPanel.add(new JLabel(si.name));
                additionalInputPanel.add(inputTextField);
                this.secondaryInputTextFields.add(inputTextField);
            }
            leftPanel.add(additionalInputPanel);
        }

        JPanel leftPanelPanel = new JPanel(); // again, don't ask
        leftPanelPanel.add(leftPanel);

        presenter.getContentPanel().setLayout(new BorderLayout());
        presenter.getContentPanel().add(leftPanelPanel, BorderLayout.WEST);
        presenter.getContentPanel().add(inputScrollPane, BorderLayout.CENTER);
        presenter.pushTerminalButton("Cancel");
        presenter.pushTerminalButton("Run");
        if (presenter.show(1) != 1) {
            return null;
        }
        this.puzzleInput.setPrimaryInput(this.inputTextArea.getText());
        this.puzzleInput.saveToJsonFile(LAST_INPUT_FILE);
        return this.puzzleInput;
    }

    private void apply(PuzzleInput p) {
        this.inputTextArea.setText(p.getPlainPrimaryInput());
        for (int i = 0; i < this.secondaryInputs.size(); ++i) {
            String secInputName = this.secondaryInputs.get(i).name;
            String secInputValue = p.getSecondaryInput(secInputName);
            this.secondaryInputTextFields.get(i).setText(secInputValue);
            this.puzzleInput.setSecondaryInput(secInputName, secInputValue);
        }
    }

    private String getFileContents(File file) {
        if (file.canRead()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                return String.join("\n", br.lines().toList());
            } catch (IOException ex) {
                System.err.printf("failed to read %s\n", file.getName());
            }
        }
        return "";
    }
}
