package client.models.ai.neural;

import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.ea.population.Population;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.genetic.MLMethodGenomeFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.obj.SerializeObject;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Neural {

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

    public static void training(MLTrain train) {
        for (int i = 0; i < 35; i++) {
            train.iteration();
            log.info("Epoch #{} score: {}", i, train.getError());
        }
        train.finishTraining();
    }

    public static void save(MLMethodGeneticAlgorithm ga, String file) throws GameException {
        try {
            ga.getGenetic().getPopulation().setGenomeFactory(null);
            SerializeObject.save(new File(file), ga.getGenetic().getPopulation());
        } catch (IOException e) {
            throw new GameException(GameErrorCode.AI_ERROR);
        }
    }

    public static MLMethodGeneticAlgorithm loadOrCreate(GameBoard board, PlayerColor color, String filename) {
        try {
            return load(board, color, filename);
        } catch (GameException e) {
            return new MLMethodGeneticAlgorithm(Neural::createNetwork, new BoardNeuralScore(color, board), 500);
        }
    }

    public static MLMethodGeneticAlgorithm load(GameBoard board, PlayerColor color, String filename) throws GameException {
        try {
            final Population pop = (Population) SerializeObject.load(new File(filename));
            pop.setGenomeFactory(new MLMethodGenomeFactory(Neural::createNetwork, pop));

            final MLMethodGeneticAlgorithm result = new MLMethodGeneticAlgorithm(Neural::createNetwork, new BoardNeuralScore(color, board), 500);

            result.getGenetic().setPopulation(pop);

            return result;
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException(GameErrorCode.AI_ERROR);
        }
    }
}
