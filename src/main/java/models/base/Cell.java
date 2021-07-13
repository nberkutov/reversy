package models.base;

public enum Cell {
    EMPTY, WHITE, BLACK;

    public static Cell valueOf(PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            return Cell.BLACK;
        }
        return Cell.WHITE;
    }

}
