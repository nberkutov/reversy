package models.base;

import exception.GameErrorCode;
import exception.GameException;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
    WHITE, BLACK, NONE;

    public PlayerColor getOpponent() throws GameException {
        if (this == NONE) {
            throw new GameException(GameErrorCode.INVALID_PLAYER_COLOR);
        }

        if (this == WHITE) {
            return BLACK;
        }
        return WHITE;
    }
}
