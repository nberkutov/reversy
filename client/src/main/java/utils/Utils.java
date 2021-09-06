package utils;

import exception.ServerException;
import logic.BoardLogic;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.List;

public class Utils {
    /**
     * Возвращает инвертированный цвет.
     */
    public static PlayerColor reverse(final PlayerColor playerColor) {
        switch (playerColor) {
            case WHITE:
                return PlayerColor.BLACK;
            case BLACK:
                return PlayerColor.WHITE;
            default:
                return PlayerColor.NONE;
        }
    }

    /**
     *   Проверяет по доске, наступил ли конец игры. Возвращает цвет победителя.
     */
    public static PlayerColor getEndOfGame(final GameBoard board) throws ServerException {
        final List<Point> blackMoves = BoardLogic.getAvailableMoves(board, PlayerColor.BLACK);
        final List<Point> whiteMoves = BoardLogic.getAvailableMoves(board, PlayerColor.WHITE);
        if (board.getCountEmpty() == 0 || blackMoves.isEmpty() || whiteMoves.isEmpty()) {
            if (board.getCountBlackCells() > board.getCountWhiteCells()) {
                return PlayerColor.BLACK;
            }
            return PlayerColor.WHITE;
        }
        return PlayerColor.NONE;
    }
}
