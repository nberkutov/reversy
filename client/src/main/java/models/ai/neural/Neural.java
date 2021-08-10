package models.ai.neural;

import exception.ServerException;
import logic.BoardLogic;
import lombok.extern.slf4j.Slf4j;
import models.base.Move;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.encog.util.obj.SerializeObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class Neural {

    public final static double MAX_ERROR = 0.01;

    private static final NormalizedField color = new NormalizedField(NormalizationAction.Normalize, "color", 1, -1, 1, -1);
    private static final NormalizedField cell = new NormalizedField(NormalizationAction.Normalize, "cell", 1, -1, 1, -1);

    private Neural() {
    }

    public static BasicNetwork createNetwork() {
        final FeedForwardPattern pattern = new FeedForwardPattern();
        pattern.setInputNeurons(65);
        pattern.addHiddenLayer(60);
        pattern.setOutputNeurons(1);
        pattern.setActivationFunction(new ActivationTANH());
        final BasicNetwork network = (BasicNetwork) pattern.generate();
        network.reset();
        return network;
    }

//    public static void training(final MLTrain train) {
//        training(train, 20);
//    }
//
//    public static void training(final MLTrain train, int count) {
//        for (int i = 0; i < count; i++) {
//            train.iteration();
//            log.info("Epoch #{} score: {}", i, train.getError());
//        }
//        train.finishTraining();
//    }

    public static Point getMove(final MLTrain train, final GameBoard board, final PlayerColor color) throws ServerException {
        final BasicNetwork network = (BasicNetwork) train.getMethod();
        final MLData input = getInput(board, color);
        final MLData output = network.compute(input);
        final double value = output.getData(0);
        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
        final NormalizedField normPoint = new NormalizedField(NormalizationAction.Normalize,
                null, points.size() - 1d, 0, 1, -1);
        final int index = (int) normPoint.normalize(value);
        return points.get(index);
    }

    public static MLDataSet generateTraining(final Map<GameBoard, Move> moves) throws ServerException {
        MLDataSet result = new BasicMLDataSet();

        for (final Map.Entry<GameBoard, Move> entry : moves.entrySet()) {
            final GameBoard board = entry.getKey();
            final Move move = entry.getValue();
            MLData inputData = getInput(board, move.getColor());
            final List<Point> points = BoardLogic.getAvailableMoves(board, move.getColor());

            int index = 0;

            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).equals(move.getPoint())) {
                    index = i;
                    break;
                }
            }

            final NormalizedField normPoint = new NormalizedField(NormalizationAction.Normalize,
                    null, points.size() - 1, 0, 1, -1);
            final MLData idealData = new BasicMLData(1);
            idealData.setData(0, normPoint.normalize(index));
            result.add(inputData, idealData);
        }

        return result;
    }

    public static void load(final ResilientPropagation train, final String path) {
        try {
            TrainingContinuation conf = (TrainingContinuation) SerializeObject.load(new File(path));
            train.resume(conf);
        } catch (IOException | ClassNotFoundException e) {
            log.info("Conf cant load {}", e.getMessage());
        }
    }

    public static ResilientPropagation training(BasicNetwork network, MLDataSet training) {
        final ResilientPropagation train = new ResilientPropagation(network, training);

        int epoch = 1;

        do {
            train.iteration();
            log.info("Epoch # {} error {}", epoch, train.getError());
            epoch++;
        } while (train.getError() > MAX_ERROR && !train.isTrainingDone());
        train.finishTraining();

        return train;
    }


//    public static void save(final MLMethodGeneticAlgorithm ga, final String file) throws ServerException {
//        final GenomeFactory tmp = ga.getGenetic().getPopulation().getGenomeFactory();
//        try {
//            ga.getGenetic().getPopulation().setGenomeFactory(null);
//            SerializeObject.save(new File(file), ga.getGenetic().getPopulation());
//        } catch (IOException e) {
//            throw new ServerException(GameErrorCode.AI_ERROR);
//        }
//        ga.getGenetic().getPopulation().setGenomeFactory(tmp);
//    }
//
//    public static MLMethodGeneticAlgorithm loadOrCreate(final BoardNeuralScore boardNeuralScore, final String filename) {
//        try {
//            return load(boardNeuralScore, filename);
//        } catch (ServerException e) {
//            return new MLMethodGeneticAlgorithm(Neural::createNetwork, boardNeuralScore, 500);
//        }
//    }
//
//    public static MLMethodGeneticAlgorithm load(final BoardNeuralScore boardNeuralScore, final String filename) throws ServerException {
//        try {
//            final Population pop = (Population) SerializeObject.load(new File(filename));
//            pop.setGenomeFactory(new MLMethodGenomeFactory(Neural::createNetwork, pop));
//
//            final MLMethodGeneticAlgorithm result = new MLMethodGeneticAlgorithm(Neural::createNetwork, boardNeuralScore, 500);
//
//            result.getGenetic().setPopulation(pop);
//
//            return result;
//        } catch (IOException | ClassNotFoundException e) {
//            throw new ServerException(GameErrorCode.AI_ERROR);
//        }
//    }

    public static MLData getInput(GameBoard board, PlayerColor moveColor) throws ServerException {
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
}
