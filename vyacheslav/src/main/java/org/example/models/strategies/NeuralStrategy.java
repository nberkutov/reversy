package org.example.models.strategies;


import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATPopulation;
import org.example.exception.ServerException;
import org.example.models.GameProperties;
import org.example.models.ai.neural.BoardNeuralScore;
import org.example.models.ai.neural.Neural;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Strategy;

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
