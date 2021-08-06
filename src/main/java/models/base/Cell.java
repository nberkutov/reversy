package models.base;

import java.io.Serializable;

public enum Cell implements Serializable {
    EMPTY(0), WHITE(-1), BLACK(1);

    private final int id;

    Cell(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Cell valueOf(PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            return Cell.BLACK;
        }
        return Cell.WHITE;
    }

}
