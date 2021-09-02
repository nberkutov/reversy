package strategy;

import exception.ServerException;
import logic.BoardLogic;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import utils.Utils;

public class Utility {
    private static final Point[] cornerPoints = {
            new Point(0, 0),
            new Point(0, 7),
            new Point(7, 0),
            new Point(7, 7)
    };

    /**
     * Комбинация эвристик, учитывающая количество фишек, занятых углов, мобильность.
     * @param board доска игры
     * @param playerColor игрок, для которого считаем полезность.
     * @return Полезность состояния (доски)
     */
    public static double multiHeuristic(final GameBoard board, final PlayerColor playerColor) {
        return coinParityHeuristic(board, playerColor)
                +  mobilityHeuristic(board, playerColor)
                +  10 * countCapturedCorners(board, playerColor);
    }

    /**
     * Эвристика, основанная на количестве фишек игрока.
     * @param board доска игры
     * @param maxPlayer игрок, для которого считаем полезность.
     * @return Полезность состояния (доски)
     */
    public static double coinParityHeuristic(final GameBoard board, final PlayerColor maxPlayer) {
        final int maxPlayerCoins;
        final int minPlayerCoins;
        if (maxPlayer == PlayerColor.BLACK) {
            maxPlayerCoins = board.getCountBlackCells();
            minPlayerCoins = board.getCountWhiteCells();
        } else {
            minPlayerCoins = board.getCountBlackCells();
            maxPlayerCoins = board.getCountWhiteCells();
        }
        return 100 * (double) (maxPlayerCoins - minPlayerCoins) / (maxPlayerCoins + minPlayerCoins);
    }

    /**
     * Эвристика, основанная на количестве доступных для игрока ходов.
     * @param board доска игры
     * @param maxPlayer игрок, для которого считаем полезность.
     * @return Полезность состояния (доски)
     */
    public static double mobilityHeuristic(final GameBoard board, final PlayerColor maxPlayer) {
        double val = 0;
        try {
            final int maxPlayerMoves;
            final int minPlayerMoves;
            if (maxPlayer == PlayerColor.BLACK) {
                maxPlayerMoves = BoardLogic.getAvailableMoves(board, Cell.BLACK).size();
                minPlayerMoves = BoardLogic.getAvailableMoves(board, Cell.WHITE).size();
            } else {
                maxPlayerMoves = BoardLogic.getAvailableMoves(board, Cell.WHITE).size();
                minPlayerMoves = BoardLogic.getAvailableMoves(board, Cell.BLACK).size();
            }
            if (maxPlayerMoves + minPlayerMoves != 0) {
                val = 100 * (double) (maxPlayerMoves - minPlayerMoves) / (maxPlayerMoves + minPlayerMoves);
            }
        } catch (final ServerException ex) {
            return val;
        }
        return val;
    }

    /**
     * Эвристика, основанная на количестве занятых углов.
     * @param board доска игры
     * @param maxPlayer игрок, для которого считаем полезность.
     * @return Полезность состояния (доски)
     */
    public static double cornerHeuristic(final GameBoard board, final PlayerColor maxPlayer) {
        final int maxPlayerCorners = countCapturedCorners(board, maxPlayer);
        final int minPlayerCorners = countCapturedCorners(board, Utils.reverse(maxPlayer));
        return 100 * (double) (maxPlayerCorners - minPlayerCorners) / (maxPlayerCorners + minPlayerCorners);
    }

    // Подсчет занятых углов
    private static int countCapturedCorners(final GameBoard board, final PlayerColor playerColor) {
        int counter = 0;
        try {
            for (final Point point : cornerPoints) {
                if (board.getCell(point) == Cell.valueOf(playerColor)) {
                    counter += 1;
                }
            }
            return counter;
        } catch (final ServerException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


