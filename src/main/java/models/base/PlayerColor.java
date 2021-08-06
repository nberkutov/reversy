package models.base;

import exception.GameErrorCode;
import exception.GameException;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
    WHITE(0), BLACK(1), NONE(-1);

    private final int id;

    PlayerColor(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

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
