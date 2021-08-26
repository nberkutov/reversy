package org.example.gui.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;

public class PiePanel extends JPanel {
    private String title;
    private DefaultPieDataset<? extends Comparable<?>> nowData;
    private JFreeChart chart;
    private ChartPanel panel;

    public PiePanel() {
        title = "";
        init();
    }

    public void update(final PieDataset<? extends Comparable<?>> updatedPieDataset) {
        updateChart(updatedPieDataset);
        panel.setChart(chart);
        updateUI();
    }

    private void updateChart(final PieDataset<? extends Comparable<?>> newData) {
        this.chart = ChartFactory.createPieChart(title, newData, false, true, true);
    }

    public void setTitle(final String title) {
        this.title = title;
        chart.setTitle(title);
    }


    private void init() {
        setLayout(new BorderLayout());
        nowData = new DefaultPieDataset<>();
        chart = ChartFactory.createPieChart(title, nowData,
                true, true, true);
        panel = new ChartPanel(chart);
        add(panel, BorderLayout.CENTER);
    }

}
