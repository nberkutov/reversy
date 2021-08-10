package models.players;

import exception.ServerException;
import logic.BoardLogic;
import lombok.EqualsAndHashCode;
import models.Player;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.NeuralStrategy;

import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode
public class AdaptiveNeuralBot extends Player {
    private final NeuralStrategy strategy;
    private final List<GameBoard> list;

    public AdaptiveNeuralBot(String nickname) {
        super(nickname);
        strategy = new NeuralStrategy();
        list = new LinkedList<>();
    }

    @Override
    public Point move(GameBoard board) throws ServerException {
        list.add(board);
        return strategy.getMove(board, color);
    }

    @Override
    public void triggerMoveOpponent(GameBoard board) throws ServerException {
        list.add(board);
    }

    @Override
    public void triggerGameEnd(GameState state, GameBoard board) throws ServerException {
        if (BoardLogic.getCountCellByPlayerColor(board, color)
                < BoardLogic.getCountCellByPlayerColor(board, color.getOpponent())) {
//            strategy.
        }
        list.clear();
    }
}
