package models;

import models.base.PlayerColor;

public enum Cell {
    EMPTY,WHITE,BLACK;

    public Cell valueOf(PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            return Cell.BLACK;
        }
        return Cell.WHITE;
    }

}
