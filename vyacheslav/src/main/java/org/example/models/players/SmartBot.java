package org.example.models.players;


import org.example.exception.ServerException;
import org.example.models.SmartPlayer;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Strategy;

public class SmartBot extends SmartPlayer {
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
    public void triggerGameEnd(final GameState state, final GameBoard board) throws ServerException {

    }
}
