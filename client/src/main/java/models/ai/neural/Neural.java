package models.ai.neural;

import exception.GameErrorCode;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.ea.genome.GenomeFactory;
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

    public static void training(final MLTrain train) {
        training(train, 20);
    }

    public static void training(final MLTrain train, int count) {
        for (int i = 0; i < count; i++) {
            train.iteration();
            log.info("Epoch #{} score: {}", i, train.getError());
        }
        train.finishTraining();
    }

    public static void save(final MLMethodGeneticAlgorithm ga, final String file) throws ServerException {
        final GenomeFactory tmp = ga.getGenetic().getPopulation().getGenomeFactory();
        try {
            ga.getGenetic().getPopulation().setGenomeFactory(null);
            SerializeObject.save(new File(file), ga.getGenetic().getPopulation());
        } catch (IOException e) {
            throw new ServerException(GameErrorCode.AI_ERROR);
        }
        ga.getGenetic().getPopulation().setGenomeFactory(tmp);
    }

    public static MLMethodGeneticAlgorithm loadOrCreate(final BoardNeuralScore boardNeuralScore, final String filename) {
        try {
            return load(boardNeuralScore, filename);
        } catch (ServerException e) {
            return new MLMethodGeneticAlgorithm(Neural::createNetwork, boardNeuralScore, 500);
        }
    }

    public static MLMethodGeneticAlgorithm load(final BoardNeuralScore boardNeuralScore, final String filename) throws ServerException {
        try {
            final Population pop = (Population) SerializeObject.load(new File(filename));
            pop.setGenomeFactory(new MLMethodGenomeFactory(Neural::createNetwork, pop));

            final MLMethodGeneticAlgorithm result = new MLMethodGeneticAlgorithm(Neural::createNetwork, boardNeuralScore, 500);

            result.getGenetic().setPopulation(pop);

            return result;
        } catch (IOException | ClassNotFoundException e) {
            throw new ServerException(GameErrorCode.AI_ERROR);
        }
    }
}
