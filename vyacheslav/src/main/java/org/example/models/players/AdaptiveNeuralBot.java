package org.example.models.players;

import lombok.EqualsAndHashCode;
import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.SmartPlayer;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.NeuralStrategy;

@EqualsAndHashCode
public class AdaptiveNeuralBot extends SmartPlayer {
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
