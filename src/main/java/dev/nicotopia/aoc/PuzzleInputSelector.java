package dev.nicotopia.aoc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme;

public class PuzzleInputSelector {
    public static final String LAST_INPUT_FILE = "last_input.json";

    private enum Status {
        WAITING, ABORTED, CONFIRMED, ERROR
    }

    private final String puzzleName;
    private final Map<String, PuzzleInput> puzzleInputPresets = new LinkedHashMap<>();
    private List<String> secondaryInputNames = new LinkedList<>();
    private final Font monospacedFont;
    private PuzzleInput puzzleInput = new PuzzleInput();
    private boolean forceAskForInput = false;
    private Status status = Status.WAITING;

    public PuzzleInputSelector(String puzzleName) {
        this.puzzleName = puzzleName;
        this.monospacedFont = Arrays
                .stream(new String[] { "DejaVu Sans Mono", "Inconsolata", "Consolas", "Monospaced", "Courier New", })
                .filter(Arrays.asList(
                        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())::contains)
                .findFirst().map(f -> new Font(f, Font.PLAIN, 14)).orElse(null);
        FlatGradiantoDeepOceanIJTheme.setup();
    }

    public void setForceAskForInput(boolean forceAskForInput) {
        this.forceAskForInput = forceAskForInput;
    }

    public Font getMonospacedFont() {
        return this.monospacedFont;
    }

    public void registerSecondaryInputs(String... names) {
        this.secondaryInputNames.clear();
        this.secondaryInputNames.addAll(Arrays.asList(names));
    }

    public void addPresetFromResource(String presetName, String primaryInputResourcePath,
            Object secondaryInputValues[]) {
        if (this.secondaryInputNames.size() != secondaryInputValues.length) {
            throw new RuntimeException(String.format(
                    "Number of registered secondary inputs (%d) does not match number of given secondary inputs (%d) for preset %s.\n",
                    this.secondaryInputNames.size(), secondaryInputValues.length, presetName));
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                PuzzleInputSelector.class.getResourceAsStream(primaryInputResourcePath)))) {
            PuzzleInput preset = this.puzzleInput.copyEmpty();
            preset.setPrimaryInput(br.lines().toList());
            for (int i = 0; i < this.secondaryInputNames.size(); ++i) {
                preset.setSecondaryInput(this.secondaryInputNames.get(i), secondaryInputValues[i].toString());
            }
            this.puzzleInputPresets.put(presetName, preset);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public PuzzleInput getPuzzleInput(Timer timer) {
        if (this.status == Status.CONFIRMED) {
            return this.puzzleInput;
        }
        timer.pause();
        PuzzleInput lastInput = PuzzleInput.fromJsonFile(LAST_INPUT_FILE);
        if (lastInput != null
                && this.secondaryInputNames.stream().allMatch(n -> lastInput.getSecondaryInput(n) != null)) {
            this.puzzleInput.copyFrom(lastInput);
            if (!this.forceAskForInput) {
                this.status = Status.CONFIRMED;
                timer.resume();
                return this.puzzleInput;
            }
            this.puzzleInputPresets.put(LAST_INPUT_FILE, lastInput);
        }

        JFrame frame = new JFrame(this.puzzleName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                PuzzleInputSelector.this.updateStatusIfWaiting(Status.ABORTED);
            }
        });

        JTextArea inputTextArea = new JTextArea(this.puzzleInput.getPlainPrimaryInput());
        inputTextArea.setFont(this.monospacedFont);

        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setPreferredSize(new Dimension(600, 600));
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("Main puzzle input"));

        JButton fromFileButton = new JButton("File source...");
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fromFileButton.addActionListener(evt -> {
            if (jfc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                inputTextArea.setText(this.getFileContents(jfc.getSelectedFile()));
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(evt -> frame.dispose());

        JButton okButton = new JButton("OK");
        okButton.addActionListener(evt -> {
            PuzzleInputSelector.this.updateStatusIfWaiting(Status.CONFIRMED);
            frame.dispose();
        });

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        List<JTextField> secondaryInputTextFields = new LinkedList<>();

        JPanel presetButtonPanel = new JPanel();
        presetButtonPanel.setLayout(new BoxLayout(presetButtonPanel, BoxLayout.Y_AXIS));
        presetButtonPanel.setBorder(BorderFactory.createTitledBorder("Use data from"));
        presetButtonPanel.add(fromFileButton);
        for (String presetName : this.puzzleInputPresets.keySet()) {
            JButton btn = new JButton(String.format(presetName));
            btn.addActionListener(evt -> {
                PuzzleInput preset = this.puzzleInputPresets.get(presetName);
                inputTextArea.setText(preset.getPlainPrimaryInput());
                for (int i = 0; i < this.secondaryInputNames.size(); ++i) {
                    String secInputName = this.secondaryInputNames.get(i);
                    String secInputValue = preset.getSecondaryInput(secInputName);
                    secondaryInputTextFields.get(i).setText(secInputValue);
                    this.puzzleInput.setSecondaryInput(secInputName, secInputValue);
                }
            });
            presetButtonPanel.add(btn);
        }
        JPanel presetButtonPanelPanel = new JPanel(); // don't ask
        presetButtonPanelPanel.add(presetButtonPanel);
        leftPanel.add(presetButtonPanelPanel);

        if (!this.secondaryInputNames.isEmpty()) {
            JPanel additionalInputPanel = new JPanel(new GridLayout(this.secondaryInputNames.size(), 2));
            for (String secInName : this.secondaryInputNames) {
                JTextField inputTextField = new JTextField(this.puzzleInput.getSecondaryInput(secInName));
                inputTextField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent evt) {
                        PuzzleInputSelector.this.puzzleInput.setSecondaryInput(secInName, inputTextField.getText());
                    }
                });
                additionalInputPanel.add(new JLabel(secInName));
                additionalInputPanel.add(inputTextField);
                secondaryInputTextFields.add(inputTextField);
            }
            leftPanel.add(additionalInputPanel);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JPanel leftPanelPanel = new JPanel(); // again, don't ask
        leftPanelPanel.add(leftPanel);

        frame.setLayout(new BorderLayout());
        frame.add(leftPanelPanel, BorderLayout.WEST);
        frame.add(inputScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        okButton.grabFocus();
        frame.setVisible(true);
        switch (this.waitForFinalStatus()) {
            case WAITING:
            case ERROR:
                throw new RuntimeException("waiting ended unexpectedly");
            case ABORTED:
                System.exit(0);
            case CONFIRMED:
                // proceed
        }

        this.puzzleInput.setPrimaryInput(inputTextArea.getText());
        this.puzzleInput.saveToJsonFile(LAST_INPUT_FILE);
        timer.resume();
        return this.puzzleInput;
    }

    private synchronized Status waitForFinalStatus() {
        while (this.status == Status.WAITING) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
                this.status = Status.ERROR;
            }
        }
        return this.status;
    }

    private synchronized void updateStatusIfWaiting(Status newStatus) {
        if (this.status == Status.WAITING) {
            this.status = newStatus;
            this.notifyAll();
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
