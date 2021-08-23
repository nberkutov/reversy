package org.example.models.base;

import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
    WHITE(0), BLACK(1), NONE(-1);

    private final int id;

    PlayerColor(final int id) {
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

    public static PlayerColor valueOf(final int id) {
        switch (id) {
            case 0:
                return WHITE;
            case 1:
                return BLACK;
            default:
                return NONE;
        }
    }
}
