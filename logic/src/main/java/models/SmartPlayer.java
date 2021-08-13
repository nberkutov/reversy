package models;

import exception.ServerException;
import models.base.GameState;
import models.base.interfaces.GameBoard;

public abstract class SmartPlayer extends Player {
    protected SmartPlayer(final String nickname) {
        super(nickname);
    }

    public abstract void triggerMoveOpponent(final GameBoard board) throws ServerException;

    public abstract void triggerGameEnd(final GameState state, final GameBoard board) throws ServerException;
}
