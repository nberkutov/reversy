package org.example.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.ServerException;
import org.example.gui.StatisticFrame;
import org.example.gui.charts.ChartsUtils;
import org.example.gui.charts.PiePanel;
import org.example.model.PostgresStatisticDao;
import org.example.model.StatsTables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Lazy
@Component(value = "updatePostgresPieCharts")
public class UpdatePostgresStatisticPieCharts {
    @Autowired
    @Qualifier(value = "stats1")
    private PiePanel piePanel;
    @Autowired
    private ChartsUtils utils;

    @Autowired
    private StatisticFrame frame;

    @Autowired
    private PostgresStatisticDao postgresStatisticDao;

    @Scheduled(fixedDelay = 1000)
    public void updateStatistic() {
        try {
            if (!frame.isVisible()) {
                return;
            }
            final List<StatsTables> list = postgresStatisticDao.getCacheTables();
            piePanel.update(utils.createPieDatasetByTables(list));
        } catch (final ServerException e) {
            log.warn("scheduled {}", e.getMessage());
        }
    }
}
