package models.base;

import exception.GameErrorCode;
import exception.GameException;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
    WHITE, BLACK, NONE;

    public static PlayerColor getOpponentColor(PlayerColor color) throws GameException {
        if (color == PlayerColor.NONE) {
            throw new GameException(GameErrorCode.INVALID_PLAYER_COLOR);
        }
        if (color == PlayerColor.BLACK) {
            return PlayerColor.WHITE;
        }
        return PlayerColor.BLACK;
    }
}
