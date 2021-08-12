package models.strategies;


import exception.ServerException;
import models.GameProperties;
import models.ai.neural.BoardNeuralScore;
import models.ai.neural.Neural;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATPopulation;

public class NeuralStrategy implements Strategy {
    private static final String path = GameProperties.NEURAL_FILE;
    private BoardNeuralScore boardNeuralScore;
    private NEATPopulation pop;
    private Genome best;

    public NeuralStrategy() {
        boardNeuralScore = new BoardNeuralScore();
        pop = Neural.loadOrCreate(path);
        best = pop.getBestGenome();
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        boardNeuralScore.update(board, color);

        training();


        if (best != null) {
            MLMethod mlMethod = pop.getCODEC().decode(pop.getBestGenome());
            return Neural.getMove((MLRegression) mlMethod, board, color);
        }
        return Neural.getMove(pop, board, color);
    }

    private void training() throws ServerException {
        try {
            Neural.training(pop, boardNeuralScore);
        } catch (RuntimeException e) {
            clearBestGenome();
            Neural.training(pop, boardNeuralScore);
        }
        Neural.save(pop, path);
        best = pop.getBestGenome();
    }

    public void clearBestGenome() {
        best = null;
        pop = Neural.createNetwork();
    }
}
