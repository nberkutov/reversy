package models.players;


import exception.ServerException;
import models.Player;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;

public class SmartBot extends Player {
    private final Strategy strategy;

    public SmartBot(final String nickname, final Strategy strategy) {
        super(nickname);
        this.strategy = strategy;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        return strategy.getMove(board, color);
    }

    @Override
    public void triggerMoveOpponent(final GameBoard board) throws ServerException {

    }

    @Override
    public void triggerGameEnd(final GameState state, final GameBoard board) {

    }

}
