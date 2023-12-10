package dev.nicotopia.aoc;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class InfoFrame {
    public static int showText(String title, String text, Font font, String... buttons) {
        FramePresenter presenter = new FramePresenter(title);

        JTextArea messageTextArea = new JTextArea(text);
        messageTextArea.setEditable(false);
        messageTextArea.setFont(font);

        JPanel iconPanel = new JPanel();
        iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 8));
        iconPanel.add(new JLabel(UIManager.getIcon("OptionPane.informationIcon")));

        JPanel framePanel = presenter.getContentPanel();
        framePanel.setLayout(new BorderLayout());
        framePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        framePanel.add(iconPanel, BorderLayout.WEST);
        framePanel.add(messageTextArea, BorderLayout.CENTER);

        Arrays.stream(buttons).forEach(presenter::pushButton);
        return presenter.show(buttons.length - 1);
    }
}
