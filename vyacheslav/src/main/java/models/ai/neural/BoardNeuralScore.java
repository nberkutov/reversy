package models.ai.neural;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;

@Data
@AllArgsConstructor
public class BoardNeuralScore implements CalculateScore {
    private PlayerColor color;
    private GameBoard board;

    public BoardNeuralScore() {
        this(PlayerColor.BLACK, new Board());
    }

    public void update(GameBoard board, PlayerColor color) {
        setBoard(board);
        setColor(color);
    }

    @SneakyThrows
    @Override
    public double calculateScore(MLMethod network) {
        final NeuralGame game = new NeuralGame((NEATNetwork) network, color, board);
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
