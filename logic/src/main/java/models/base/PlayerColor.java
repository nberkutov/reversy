package models.base;

import exception.GameErrorCode;
import exception.ServerException;

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

    public PlayerColor getOpponent() throws ServerException {
        if (this == NONE) {
            throw new ServerException(GameErrorCode.INVALID_PLAYER_COLOR);
        }

        if (this == WHITE) {
            return BLACK;
        }
        return WHITE;
    }
}
