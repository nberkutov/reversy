package org.example.gui.charts;

import org.example.model.StatsTables;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChartsUtils {
    public DefaultPieDataset<? extends Comparable<?>> createPieDatasetByTables(final List<StatsTables> list) {
        final DefaultPieDataset<StatsTables> dataset = new DefaultPieDataset<>();
        for (final StatsTables stats : list) {
            dataset.setValue(stats, stats.getBytes());
        }
        return dataset;
    }
}
