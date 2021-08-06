package client.models.ai.neural;

import lombok.SneakyThrows;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;

public class BoardNeuralScore implements CalculateScore {
    private PlayerColor color;
    private GameBoard board;

    public BoardNeuralScore(PlayerColor color, GameBoard board) {
        this.color = color;
        this.board = board;
    }

    @SneakyThrows
    @Override
    public double calculateScore(MLMethod network) {
        final NeuralGame game = new NeuralGame((BasicNetwork) network, color, board);
        return game.scoreGame();
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }
}
