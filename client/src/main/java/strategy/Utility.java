package strategy;

import exception.ServerException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

public class Utility {
    private static final Point[] cornerPoints = {
            new Point(0, 0),
            new Point(0, 7),
            new Point(7, 0),
            new Point(7, 7)
    };

    public static int simple(final GameBoard board, final PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLACK) {
            return board.getCountBlackCells() - board.getCountWhiteCells();
        }
        return board.getCountWhiteCells() - board.getCountBlackCells();
    }

    public static int advanced(final GameBoard board, final PlayerColor playerColor) {
        final int cornerPawnWeight = 10;
        int estimation = 0;
        try {
            for (final Point point : cornerPoints) {
                if (board.getCell(point) == Cell.valueOf(playerColor)) {
                    estimation += cornerPawnWeight;
                }
            }
        } catch (final ServerException e) {
            e.printStackTrace();
        }
        int pawnDifference = board.getCountBlackCells() - board.getCountWhiteCells();
        if (playerColor == PlayerColor.WHITE) {
            pawnDifference *= -1;
        }
        estimation += pawnDifference;
        return estimation;
    }
}
