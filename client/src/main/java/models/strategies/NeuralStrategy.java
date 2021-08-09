package models.strategies;


import exception.ServerException;
import models.GameProperties;
import models.ai.neural.BoardNeuralScore;
import models.ai.neural.Neural;
import models.ai.neural.NeuralGame;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.neural.networks.BasicNetwork;

public class NeuralStrategy implements Strategy {
    private static final String path = GameProperties.NEURAL_FILE;

    private BoardNeuralScore boardNeuralScore;
    private MLMethodGeneticAlgorithm train;
    private boolean needTrain;

    public NeuralStrategy(boolean needTrain) {
        this.needTrain = needTrain;
        boardNeuralScore = new BoardNeuralScore();
        train = Neural.loadOrCreate(boardNeuralScore, path);
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        boardNeuralScore.update(board, color);
        training();
        final NeuralGame neuralGame = new NeuralGame((BasicNetwork) train.getMethod(), color, board);
        return neuralGame.getNeuralMove();
    }

    private void training() throws ServerException {
        if (needTrain) {
            Neural.training(train);
            Neural.save(train, path);
            train = Neural.load(boardNeuralScore, path);
        }
    }
}
