package org.example.gui;

import org.example.gui.charts.PiePanel;
import org.example.gui.charts.TimeSeriesPanel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Lazy
@Component
public class StatisticFrame extends JFrame {
    private GridLayout layout;

    public StatisticFrame() {
        layout = new GridLayout(2, 2, 1, 1);
        init();
    }

    @Lazy
    @Bean(name = "stats1")
    public PiePanel createStatsTable() {
        final PiePanel pie = new PiePanel();
        pie.setTitle("Диаграмма таблиц в PostgreSQL (Таблица/Размер). Топ 10");
        addPanel(pie);
        return pie;
    }


    @Lazy
    @Bean(name = "stats2")
    public TimeSeriesPanel createStatsTransactions() {
        final TimeSeriesPanel panel = new TimeSeriesPanel("Transactions per second", "Время", "Транзакции");
        addPanel(panel);
        return panel;
    }

    @Lazy
    @Bean(name = "stats3")
    public TimeSeriesPanel createStatsOperation() {
        final TimeSeriesPanel panel = new TimeSeriesPanel("Кол-во Insert и Update в секунду", "Время", "Операции");
        addPanel(panel);
        return panel;
    }

    @Lazy
    @Bean(name = "stats4")
    public TimeSeriesPanel createStatsSelects() {
        final TimeSeriesPanel panel = new TimeSeriesPanel("Кол-во Select в секунду", "Время", "Операции");
        addPanel(panel);
        return panel;
    }

    public void addPanel(final JPanel panel) {
        add(panel);
    }

    private void init() {
        setLayout(layout);
        setTitle("Мониторинг");
        setSize(new Dimension(1200, 900));
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
