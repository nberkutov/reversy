package models.base;


import exception.ServerException;
import models.Player;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;

public class SmartBot extends Player {
    private final Strategy strategy;

    public SmartBot(String nickname, Strategy strategy) {
        super(nickname);
        this.strategy = strategy;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        return strategy.getMove(board, color);
    }

    @Override
    public void triggerAfterGameEnd(GameState state, GameBoard board) {

    }

}
