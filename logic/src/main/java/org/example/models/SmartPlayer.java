package org.example.models;

import org.example.exception.ServerException;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;

public abstract class SmartPlayer extends Player {
    protected SmartPlayer(final String nickname) {
        super(nickname);
    }

    public abstract void triggerMoveOpponent(final GameBoard board) throws ServerException;

    public abstract void triggerGameEnd(final GameState state, final GameBoard board) throws ServerException;
}
