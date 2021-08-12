package models.ai.neural;

import exception.ServerException;
import logic.BoardLogic;
import lombok.extern.slf4j.Slf4j;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.persist.PersistError;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.io.File;
import java.util.List;

@Slf4j
public class Neural {

    private static final NormalizedField color = new NormalizedField(NormalizationAction.Normalize, "color", 1, -1, 1, -1);
    private static final NormalizedField cell = new NormalizedField(NormalizationAction.Normalize, "cell", 1, -1, 1, -1);

    private Neural() {
    }

    public static NEATPopulation createNetwork() {
        NEATPopulation population = new NEATPopulation(65, 1, 50);
        population.reset();
        return population;
    }

    public static Point getMove(final MLRegression mlMethod, final GameBoard board, final PlayerColor color) throws ServerException {
        final MLData input = getInput(board, color);
        final MLData output = mlMethod.compute(input);
        final double value = output.getData(0);
        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
        final NormalizedField normPoint = new NormalizedField(NormalizationAction.Normalize,
                null, points.size() - 1d, 0, 1, -1);
        final int index = (int) normPoint.deNormalize(value);

        return points.get(index);
    }

    public static Point getMove(final NEATPopulation pop, final GameBoard board, final PlayerColor color) throws ServerException {
        final MLData input = getInput(board, color);
        final MLData output = pop.compute(input);
        final double value = output.getData(0);
        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
        final NormalizedField normPoint = new NormalizedField(NormalizationAction.Normalize,
                null, points.size() - 1d, 0, 1, -1);
        final int index = (int) normPoint.deNormalize(value);

        return points.get(index);
    }

    public static void training(final NEATPopulation pop, final BoardNeuralScore score) {
        final EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop, score);

        int epoch = 1;

        int popSize = pop.getPopulationSize();
        do {

            train.iteration();

            log.info("Epoch # {} error {}", epoch, train.getError());
            if (train.getError() == 0) {
                pop.setPopulationSize(pop.getPopulationSize() + 50);
            }
            epoch++;
        } while (train.getError() == 0 && epoch < 50);
        pop.setPopulationSize(popSize);
    }

    public static MLData getInput(final GameBoard board, final PlayerColor moveColor) throws ServerException {
        final MLData input = new BasicMLData(1 + board.getSize() * board.getSize());
        input.setData(0, color.normalize(moveColor.getId()));
        int k = 1;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                input.setData(k++, cell.normalize(board.getCell(i, j).getId()));
            }
        }
        return input;
    }

    public static void save(final NEATPopulation pop, final String path) {
        EncogDirectoryPersistence.saveObject(new File(path), pop);
    }

    public static NEATPopulation loadOrCreate(final String path) {
        try {
            return (NEATPopulation) EncogDirectoryPersistence.loadObject(new File(path));
        } catch (PersistError e) {
            return createNetwork();
        }

    }
}
