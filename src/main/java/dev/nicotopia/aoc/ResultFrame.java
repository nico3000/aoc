package dev.nicotopia.aoc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class ResultFrame {
    public static void showResults(String text, Font font) {
        JTextArea resultsTextArea = new JTextArea(text);
        resultsTextArea.setEditable(false);
        resultsTextArea.setFont(font);

        JFrame frame = new JFrame("Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BorderLayout());
        framePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel iconPanel = new JPanel();
        iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 8));
        iconPanel.add(new JLabel(UIManager.getIcon("OptionPane.informationIcon")));

        JPanel resultsPanel = new JPanel();
        resultsPanel.add(resultsTextArea);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> frame.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);

        framePanel.add(iconPanel, BorderLayout.WEST);
        framePanel.add(resultsPanel, BorderLayout.CENTER);
        framePanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(framePanel);
        frame.pack();
        okButton.grabFocus();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
