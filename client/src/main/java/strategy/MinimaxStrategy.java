package strategy;

import base.Strategy;
import exception.ServerException;
import logic.BoardLogic;
import models.ArrayBoard;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import utils.Utils;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class MinimaxStrategy implements Strategy {
    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private PlayerColor color;

    /**
     * Алгоритм Минимакс.
     *
     * @param depth   максимальная глубина дерева.
     * @param utility функция полезности.
     */
    public MinimaxStrategy(final int depth, final ToDoubleBiFunction<GameBoard, PlayerColor> utility) {
        this.depth = depth;
        this.utility = utility;
    }

    public void setColor(final PlayerColor color) {
        this.color = color;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = BoardLogic.getAvailableMoves(board, color);
        double maxWin = Integer.MIN_VALUE;
        Point maxMove = moves.get(0);
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final double win = minimax(boardCopy, depth, color);
            if (win > maxWin) {
                maxWin = win;
                maxMove = move;
            }
        }
        return maxMove;
    }

    private double minimax(final GameBoard board, final int depth, final PlayerColor currentColor) throws ServerException {
        final ToDoubleBiFunction<GameBoard, PlayerColor> estimateFunc;
        if (currentColor == color) {
            estimateFunc = utility;
        } else {
            estimateFunc = (b, c) -> -utility.applyAsDouble(b, c);
        }
        final PlayerColor winner = Utils.getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return estimateFunc.applyAsDouble(board, currentColor);
        }
        final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, currentColor);
        double maxWin = Integer.MIN_VALUE;
        for (final Point move : availableMoves) {
            final GameBoard copy = new ArrayBoard(board);
            BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
            final double win = minimax(copy, depth - 1, Utils.reverse(currentColor));
            maxWin = Math.max(win, maxWin);
        }
        return maxWin;
    }
}
