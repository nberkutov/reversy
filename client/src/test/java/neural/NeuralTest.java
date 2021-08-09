package neural;

import exception.ServerException;
import models.GameProperties;
import models.ai.neural.BoardNeuralScore;
import models.ai.neural.Neural;
import models.ai.neural.NeuralGame;
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
    void testTrain() throws ServerException {
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
    void testTrainSaveAndLoad() throws ServerException {
        final Board board = new Board();
        final PlayerColor color = PlayerColor.BLACK;
        final BoardNeuralScore score = new BoardNeuralScore(color, board);
        final MLMethodGeneticAlgorithm train = new MLMethodGeneticAlgorithm(Neural::createNetwork, score, 500);

        Neural.training(train);

        Neural.save(train, NEURAL_PATH);

        final MLMethodGeneticAlgorithm train2 = Neural.load(new BoardNeuralScore(color, board), NEURAL_PATH);

        Neural.training(train2);

        final NeuralGame neuralGame = new NeuralGame((BasicNetwork) train2.getMethod(), color, board);
        assertNotNull(neuralGame.getNeuralMove());

        Encog.getInstance().shutdown();
    }

}