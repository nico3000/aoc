package dev.nicotopia.aoc;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class Dialog {
    public static Dialog createInfoDialog(String title, String text, Font font, String... terminalButtons) {
        return new Dialog(title, text, font, UIManager.getIcon("OptionPane.informationIcon"), terminalButtons);
    }

    public static int showInfo(String title, String text, Font font, String... terminalButtons) {
        return Dialog.createInfoDialog(title, text, font, terminalButtons).show();
    }

    public static int showError(String title, String text, Font font, String... terminalButtons) {
        return new Dialog(title, text, font, UIManager.getIcon("OptionPane.errorIcon"), terminalButtons).show();
    }

    public static boolean showYesNoQuestion(String title, String message) {
        return new Dialog(title, message, DayBase.DEFAULT_FONT, UIManager.getIcon("OptionPane.questionIcon"), "Yes",
                "No").show() == 0;
    }

    public static boolean showYesNoWarning(String title, String message) {
        return new Dialog(title, message, DayBase.DEFAULT_FONT, UIManager.getIcon("OptionPane.warningIcon"), "Yes",
                "No").show() == 0;
    }

    public static void showImage(String title, ImageComponent imageComponent) {
        Dialog d = new Dialog(title, imageComponent, null);
        d.pushButton("Save...", () -> Dialog.saveImage(imageComponent.getImage(), d.getFrame()));
        d.pushTerminalButton("Close");
        d.show();
    }

    private static void saveImage(BufferedImage image, JFrame parentFrame) {
        String[] suffixes = Arrays.stream(ImageIO.getWriterFormatNames()).map(String::toLowerCase).distinct()
                .toArray(String[]::new);
        if (suffixes.length == 0) {
            Dialog.showError("Error", "No image writers available.", DayBase.DEFAULT_FONT, "OK");
            return;
        }
        String defaultSuffix = Arrays.stream(suffixes).filter(s -> s.equals("png")).findAny().orElse(suffixes[0]);
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || Arrays.stream(suffixes).anyMatch(s -> f.getName().endsWith("." + s));
            }

            @Override
            public String getDescription() {
                return String.format("Image (%s)",
                        Arrays.stream(suffixes).map(s -> "*." + s).collect(Collectors.joining(", ")));
            }
        });
        if (jfc.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            int suffixPos = f.getName().lastIndexOf('.');
            String suffix = suffixPos == -1 ? null : f.getName().substring(suffixPos + 1).toLowerCase();
            if (suffix == null || !Arrays.asList(suffixes).contains(suffix)) {
                f = new File(f.getName() + "." + defaultSuffix);
                suffix = defaultSuffix;
            }
            if (!f.exists() || Dialog.showYesNoWarning("Overwrite",
                    String.format("The file %s already exists. Overwrite?", f.getName()))) {
                try {
                    ImageIO.write(image, suffix, f);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static JComponent getTextComponent(String text, Font font) {
        JTextArea messageTextArea = new JTextArea(text);
        messageTextArea.setEditable(false);
        messageTextArea.setFont(font);
        return messageTextArea;
    }

    private final FramePresenter presenter;
    private int numButtons = 0;

    public Dialog(String title, String text, Font font, Icon icon, String... terminalButtons) {
        this(title, Dialog.getTextComponent(text, font), icon, terminalButtons);
    }

    public Dialog(String title, JComponent content, Icon icon, String... terminalButtons) {
        this.presenter = new FramePresenter(title);

        JPanel framePanel = this.presenter.getContentPanel();
        framePanel.setLayout(new BorderLayout());
        framePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        framePanel.add(content, BorderLayout.CENTER);

        if (icon != null) {
            JPanel iconPanel = new JPanel();
            iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 8));
            iconPanel.add(new JLabel(icon));
            framePanel.add(iconPanel, BorderLayout.WEST);
        }

        Arrays.stream(terminalButtons).forEach(this.presenter::pushTerminalButton);
        this.numButtons = terminalButtons.length;
    }

    public int show() {
        return this.presenter.show(this.numButtons - 1);
    }

    public void pushTerminalButton(String button) {
        ++this.numButtons;
        this.presenter.pushTerminalButton(button);
    }

    public void pushButton(String button, Runnable action) {
        ++this.numButtons;
        this.presenter.pushButton(button, action);
    }

    public JFrame getFrame() {
        return this.presenter.getFrame();
    }
}
