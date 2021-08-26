package org.example.gui.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class TimeSeriesPanel extends JPanel {
    private final String title;
    private final String xLabel;
    private final String yLabel;
    private final TimeSeriesCollection dataset;
    private ChartPanel panel;

    public TimeSeriesPanel(final String title, final String xLabel, final String yLabel) {
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        dataset = new TimeSeriesCollection();
        init();
    }

    private static void settingChart(final JFreeChart chart) {
        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        final XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setDrawSeriesLineAsPath(true);
        }

        final DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("mm:ss"));

    }

    public void addSeries(final TimeSeries ts) {
        dataset.addSeries(ts);
    }

    private void init() {
        setLayout(new BorderLayout());
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title, xLabel, yLabel,
                dataset,
                true, true, true);
        settingChart(chart);
        panel = new ChartPanel(chart);
        add(panel, BorderLayout.CENTER);
    }
}
