package dev.nicotopia.aoc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.OptionalInt;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FramePresenter {
    private final JFrame frame;
    private final JPanel contentPanel;
    private final JPanel buttonPanel;
    private OptionalInt pressedButtonIdx = OptionalInt.empty();

    public FramePresenter(String title) {
        this.frame = new JFrame(title);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.contentPanel = new JPanel();
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.frame.setLayout(new BorderLayout());
        this.frame.add(this.contentPanel, BorderLayout.CENTER);
        this.frame.add(this.buttonPanel, BorderLayout.SOUTH);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FramePresenter.this.notifyButtonPressed(-1);
            }
        });
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public JPanel getContentPanel() {
        return this.contentPanel;
    }

    public void pushButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(evt -> new Thread(action, label + "_thread").start());
        this.buttonPanel.add(button);
    }

    public void pushTerminalButton(String label) {
        int buttonIdx = this.buttonPanel.getComponentCount();
        this.pushButton(label, () -> {
            FramePresenter.this.frame.dispose();
            FramePresenter.this.notifyButtonPressed(buttonIdx);
        });
    }

    public int show() {
        return this.show(OptionalInt.empty());
    }

    public int show(int focusedButton) {
        return this.show(OptionalInt.of(focusedButton));
    }

    public int show(OptionalInt focusedButton) {
        frame.pack();
        frame.setLocationRelativeTo(null);
        focusedButton.ifPresent(i -> ((JButton) this.buttonPanel.getComponent(i)).grabFocus());
        frame.setVisible(true);
        try {
            return this.waitForButtonPress();
        } catch (InterruptedException ex) {
            System.err.println(ex);
            frame.dispose();
            return -1;
        }
    }

    private synchronized int waitForButtonPress() throws InterruptedException {
        while (this.pressedButtonIdx.isEmpty()) {
            this.wait();
        }
        return this.pressedButtonIdx.getAsInt();
    }

    private synchronized void notifyButtonPressed(int buttonIdx) {
        if (this.pressedButtonIdx.isEmpty()) {
            this.pressedButtonIdx = OptionalInt.of(buttonIdx);
        }
        this.notifyAll();
    }
}
