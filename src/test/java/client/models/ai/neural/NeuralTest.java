package client.models.ai.neural;

import exception.GameException;
import models.GameProperties;
import models.base.PlayerColor;
import models.board.Board;
import org.encog.Encog;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NeuralTest {
    private static final String NEURAL_PATH = GameProperties.NEURAL_FILE;


    @Test
    void testTrain() throws GameException {
        final Board board = new Board();
        final PlayerColor color = PlayerColor.BLACK;

        final MLTrain train = new MLMethodGeneticAlgorithm(Neural::createNetwork, new BoardNeuralScore(color, board), 500);

        Neural.training(train);

        final BasicNetwork network = (BasicNetwork) train.getMethod();

        final NeuralGame neuralGame = new NeuralGame(network, color, board);
        assertNotNull(neuralGame.getNeuralMove());

        Encog.getInstance().shutdown();
    }

    @Test
    void testTrainSaveAndLoad() throws GameException {
        final Board board = new Board();
        final PlayerColor color = PlayerColor.BLACK;

        final MLMethodGeneticAlgorithm train = new MLMethodGeneticAlgorithm(Neural::createNetwork, new BoardNeuralScore(color, board), 500);

        Neural.training(train);

        Neural.save(train, NEURAL_PATH);

        final MLMethodGeneticAlgorithm train2 = Neural.load(board, color, NEURAL_PATH);

        Neural.training(train2);

        final NeuralGame neuralGame = new NeuralGame((BasicNetwork) train2.getMethod(), color, board);
        assertNotNull(neuralGame.getNeuralMove());

        Encog.getInstance().shutdown();
    }

}