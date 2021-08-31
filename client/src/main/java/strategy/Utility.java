package strategy;

import exception.ServerException;
import logic.BoardLogic;
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
        return Math.abs(board.getCountWhiteCells() - board.getCountBlackCells());
    }

    public static int advanced(final GameBoard board, final PlayerColor playerColor) {
        final int cornerPawnWeight = 20;
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

    public static double multiHeuristic(final GameBoard board, final PlayerColor playerColor) {
        return coinParityHeuristic(board, playerColor)
                +  mobilityHeuristic(board, playerColor)
                +  10 * countCapturedCorners(board, playerColor);
    }

    // Coin Parity Heuristic Value =
    //	100 * (Max Player Coins - Min Player Coins ) / (Max Player Coins + Min Player Coins)
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

    // Mobility Heuristic Value =
    //		100 * (Max Player Moves - Min Player Moves) / (Max Player Moves + Min Player Moves)
    //else
    //	Mobility Heuristic Value = 0
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

    //if ( Max Player Corners + Min Player Corners != 0)
    //	Corner Heuristic Value =
    //		100 * (Max Player Corners - Min Player Corners) / (Max Player Corners + Min Player Corners)
    //else
    //	Corner Heuristic Value = 0
    public static double cornerHeuristic(final GameBoard board, final PlayerColor maxPlayer) {
        final int maxPlayerCorners = countCapturedCorners(board, maxPlayer);
        final int minPlayerCorners = countCapturedCorners(board, revert(maxPlayer));
        return 100 * (double) (maxPlayerCorners - minPlayerCorners) / (maxPlayerCorners + minPlayerCorners);
    }

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


    private static PlayerColor revert(final PlayerColor color) {
        switch (color) {
            case WHITE:
                return PlayerColor.BLACK;

            case BLACK:
                return PlayerColor.WHITE;

            default:
                return color;
        }
    }
}


