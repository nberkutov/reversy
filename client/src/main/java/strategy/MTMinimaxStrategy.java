package strategy;

import base.Strategy;
import exception.ServerException;
import logic.BoardLogic;
import lombok.SneakyThrows;
import models.ArrayBoard;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.ToDoubleBiFunction;

public class MTMinimaxStrategy implements Strategy {
    private static final class MinimaxValue extends RecursiveTask<Double> {
        private final GameBoard board;
        private final int depth;
        private final PlayerColor color;
        private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
        private final ForkJoinPool forkJoinPool;

        public MinimaxValue(
                final ForkJoinPool forkJoinPool,
                final GameBoard board,
                final int depth,
                final PlayerColor color,
                final ToDoubleBiFunction<GameBoard, PlayerColor> utility
        ) {
            this.board = board;
            this.depth = depth;
            this.color = color;
            this.utility = utility;
            this.forkJoinPool = forkJoinPool;
        }

        @SneakyThrows
        @Override
        protected Double compute() {
            return minimax(board, depth, color);
        }

        private double minimax(final GameBoard board, final int depth, final PlayerColor currentColor)
                throws ServerException {
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
            final List<MinimaxValue> subtasks = new ArrayList<>();
            double maxWin = Integer.MIN_VALUE;
            for (final Point move : availableMoves) {
                final GameBoard copy = new ArrayBoard(board);
                BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
                final MinimaxValue val =
                        new MinimaxValue(forkJoinPool, copy, depth - 1, Utils.reverse(color), utility);
                val.fork();
                subtasks.add(val);
            }
            for (final MinimaxValue task : subtasks) {
                final double win = task.join();
                maxWin = Math.max(win, maxWin);
            }
            return maxWin;
        }
    }

    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private final ForkJoinPool forkJoinPool;
    private PlayerColor color;

    /**
     * Алгоритм Минимакс с использованием ForkJoin Pool.
     *
     * @param depth   максимальная глубина дерева.
     * @param utility функция полезности.
     */
    public MTMinimaxStrategy(final int depth, final ToDoubleBiFunction<GameBoard, PlayerColor> utility) {
        this.depth = depth;
        this.utility = utility;
        forkJoinPool = new ForkJoinPool(6);
    }

    public void setColor(final PlayerColor color) {
        this.color = color;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = BoardLogic.getAvailableMoves(board, color);
        double maxWin = Double.MIN_VALUE;
        Point maxMove = moves.get(0);
        final List<MinimaxValue> subtasks = new ArrayList<>();
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final MinimaxValue val = new MinimaxValue(forkJoinPool, boardCopy, depth, Utils.reverse(color), utility);
            forkJoinPool.submit(val);
            subtasks.add(val);
        }
        for (final MinimaxValue task : subtasks) {
            final double win = task.join();
            if (win > maxWin) {
                maxWin = win;
                maxMove = moves.get(subtasks.indexOf(task));
            }
        }
        return maxMove;
    }
}