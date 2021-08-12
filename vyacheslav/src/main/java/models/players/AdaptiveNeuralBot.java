package models.players;

import exception.ServerException;
import logic.BoardLogic;
import lombok.EqualsAndHashCode;
import models.Player;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.NeuralStrategy;

@EqualsAndHashCode
public class AdaptiveNeuralBot extends Player {
    private final NeuralStrategy strategy;

    public AdaptiveNeuralBot(final String nickname) {
        super(nickname);
        strategy = new NeuralStrategy();
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        return strategy.getMove(board, color);
    }

    @Override
    public void triggerMoveOpponent(final GameBoard board) throws ServerException {
    }

    @Override
    public void triggerGameEnd(final GameState state, final GameBoard board) throws ServerException {
        if (BoardLogic.getCountCellByPlayerColor(board, color)
                < BoardLogic.getCountCellByPlayerColor(board, color.getOpponent())) {
            strategy.clearBestGenome();
        }
    }
}
