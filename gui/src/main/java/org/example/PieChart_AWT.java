package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class PieChart_AWT extends JFrame {

    public PieChart_AWT(String title) {
        super(title);
        setContentPane(createDemoPanel());
    }

//    private static PieDataset createDataset( ) {
//        DefaultPieDataset dataset = new DefaultPieDataset( );
//        dataset.setValue( "IPhone 5s" , new Double( 20 ) );
//        dataset.setValue( "SamSung Grand" , new Double( 20 ) );
//        dataset.setValue( "MotoG" , new Double( 40 ) );
//        dataset.setValue( "Nokia Lumia" , new Double( 10 ) );
//        return dataset;
//    }

    private static XYDataset createDataset() {
        TimeSeries s1 = new TimeSeries("График №1");
        s1.add(new Month(7, 2013), 142.9);
        s1.add(new Month(8, 2013), 138.7);
        s1.add(new Month(9, 2013), 137.3);
        s1.add(new Month(10, 2013), 143.9);
        s1.add(new Month(11, 2013), 139.8);
        s1.add(new Month(12, 2013), 137.0);
        s1.add(new Month(1, 2014), 132.8);
        s1.add(new Month(2, 2014), 181.8);
        s1.add(new Month(3, 2014), 167.3);
        s1.add(new Month(4, 2014), 153.8);
        s1.add(new Month(5, 2014), 167.6);
        s1.add(new Month(6, 2014), 158.8);
        s1.add(new Month(7, 2014), 148.3);
        s1.add(new Month(8, 2014), 153.9);
        s1.add(new Month(9, 2014), 142.7);
        s1.add(new Month(10, 2014), 123.2);
        s1.add(new Month(11, 2014), 131.8);
        s1.add(new Month(12, 2014), 139.6);

        TimeSeries s2 = new TimeSeries("График №2");
        s2.add(new Month(7, 2013), 111.7);
        s2.add(new Month(8, 2013), 111.0);
        s2.add(new Month(9, 2013), 109.6);
        s2.add(new Month(10, 2013), 113.2);
        s2.add(new Month(11, 2013), 111.6);
        s2.add(new Month(12, 2013), 108.8);
        s2.add(new Month(1, 2014), 101.6);
        s2.add(new Month(2, 2014), 129.6);
        s2.add(new Month(3, 2014), 123.2);
        s2.add(new Month(4, 2014), 117.2);
        s2.add(new Month(5, 2014), 124.1);
        s2.add(new Month(6, 2014), 122.6);
        s2.add(new Month(7, 2014), 119.2);
        s2.add(new Month(8, 2014), 116.5);
        s2.add(new Month(9, 2014), 112.7);
        s2.add(new Month(10, 2014), 101.5);
        s2.add(new Month(11, 2014), 106.1);
        s2.add(new Month(12, 2014), 110.3);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;
    }

    private static JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Семейный доход за текущий год",
                null,                   // x-axis label
                "Доход",                // y-axis label
                dataset);
        chart.addSubtitle(new TextTitle("В доходе включен только " +
                "заработок по основной работе"));
        chart.setBackgroundPaint(Color.white);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        chart.getLegend().setFrame(BlockBorder.NONE);

        return chart;
    }
//
//    private static JFreeChart createChart(PieDataset dataset ) {
//        JFreeChart chart = ChartFactory.createPieChart(
//                "Mobile Sales",   // chart title
//                dataset,          // data
//                true,             // include legend
//                true,
//                false);
//
//        return chart;
//    }

    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Доходы от рекламы на сайте",  // title
                "",                            // x-axis label
                "Валюта",                      // y-axis label
                dataset,                       // data
                true,                          // create legend
                true,                          // generate tooltips
                false                          // generate URLs
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
//            renderer.setBaseShapesVisible   (true);
//            renderer.setBaseShapesFilled    (true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MM.YYYY"));

        return chart;
    }

    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    public static void main(String[] args) {
        PieChart_AWT demo = new PieChart_AWT("Mobile Sales");
        demo.setSize(560, 367);
//        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible(true);
    }
}