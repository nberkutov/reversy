package models.base;

import java.io.Serializable;

public enum Cell implements Serializable {
    EMPTY, WHITE, BLACK;

    public static Cell valueOf(PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            return Cell.BLACK;
        }
        return Cell.WHITE;
    }

}
