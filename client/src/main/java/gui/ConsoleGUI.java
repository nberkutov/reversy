package gui;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class ConsoleGUI extends JFrame {
    private final JPanel mainPanel;
    private final JTextArea textArea;
    private final JScrollPane scrollPane;
    private final JPanel panel;
    private final JButton buttonClear;

    public ConsoleGUI() {
        mainPanel = new JPanel();
        textArea = new JTextArea(10, 20);
        scrollPane = new JScrollPane(textArea);
        panel = new JPanel();
        buttonClear = new JButton("Очистить");
        setLayoutManager();
        addComponentsToJFrame();
        addActionEvent();
        init();
    }

    private void init() {
        setTitle("Console");
        setPreferredSize(new Dimension(350, 200));
        pack();
        setLocationRelativeTo(null);
        setVisible(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        textArea.setEditable(false);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private void setLayoutManager() {
        mainPanel.setLayout(new BorderLayout());
        panel.setLayout(new FlowLayout());
    }

    private void addComponentsToJFrame() {
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonClear);
        mainPanel.add(panel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public void addString(final String string) {
        textArea.append(string + "\n");
    }

    private void addActionEvent() {
        buttonClear.addActionListener(e -> {
            textArea.setText("");
        });
    }
}
