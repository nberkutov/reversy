package org.example.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.ServerException;
import org.example.gui.StatisticFrame;
import org.example.gui.charts.TimeSeriesPanel;
import org.example.model.PostgresStatisticDao;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component(value = "updatePostgresXYCharts")
public class UpdatePostgresStatisticXYCharts {
    @Autowired
    @Qualifier(value = "stats2")
    private TimeSeriesPanel panelTransactions;

    @Autowired
    @Qualifier(value = "stats3")
    private TimeSeriesPanel panelOperations;

    @Autowired
    @Qualifier(value = "stats4")
    private TimeSeriesPanel panelSelect;
    @Autowired
    private StatisticFrame frame;
    @Autowired
    private PostgresStatisticDao postgresStatisticDao;
    @Autowired
    @Qualifier(value = "timeSCommits")
    private TimeSeries commitsSeries;

    @Autowired
    @Qualifier(value = "timeSUpdate")
    private TimeSeries updateSeries;

    @Autowired
    @Qualifier(value = "timeSInsert")
    private TimeSeries insertSeries;

    @Autowired
    @Qualifier(value = "timeSSelect")
    private TimeSeries selectSeries;

    @Bean("timeSCommits")
    public TimeSeries createCommitSeries() {
        final TimeSeries ts = new TimeSeries("Commits");
        panelTransactions.addSeries(ts);
        return ts;
    }

    @Bean("timeSUpdate")
    public TimeSeries createUpdateSeries() {
        final TimeSeries ts = new TimeSeries("Update");
        panelOperations.addSeries(ts);
        return ts;
    }

    @Bean("timeSInsert")
    public TimeSeries createInsertSeries() {
        final TimeSeries ts = new TimeSeries("Insert");
        panelOperations.addSeries(ts);
        return ts;
    }

    @Bean("timeSSelect")
    public TimeSeries createSelectSeries() {
        final TimeSeries ts = new TimeSeries("Select");
        panelSelect.addSeries(ts);
        return ts;
    }

    @Scheduled(fixedDelay = 1000)
    public void updateStatistic() {
        try {
            if (!frame.isVisible()) {
                return;
            }
            final long commits = postgresStatisticDao.getSumCommits();
            final long selects = postgresStatisticDao.getSumSelectsNow();
            final long update = postgresStatisticDao.getSumUpdateNow();
            final long insert = postgresStatisticDao.getSumInsertNow();
            Thread.sleep(1000);
            final long commitsAfterSec = postgresStatisticDao.getSumCommits();
            final long selectsAfterSec = postgresStatisticDao.getSumSelectsNow();
            final long updateAfterSec = postgresStatisticDao.getSumUpdateNow();
            final long insertAfterSec = postgresStatisticDao.getSumInsertNow();
            final long commitsPerSec = commitsAfterSec - commits;
            final long selectsPerSec = selectsAfterSec - selects;
            final long updatePerSec = updateAfterSec - update;
            final long insertPerSec = insertAfterSec - insert;

            commitsSeries.add(new Second(), commitsPerSec);
            selectSeries.add(new Second(), selectsPerSec);
            updateSeries.add(new Second(), updatePerSec);
            insertSeries.add(new Second(), insertPerSec);
        } catch (final ServerException e) {
            log.error("scheduled {}", e.getMessage());
        } catch (final InterruptedException e) {
            log.warn("scheduled finished {}", e.getMessage());
        }
    }
}
