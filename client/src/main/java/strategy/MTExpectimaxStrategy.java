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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.ToDoubleBiFunction;

public class MTExpectimaxStrategy implements Strategy {
    private static class ExpectimaxValue extends RecursiveTask<Double> {
        private final GameBoard board;
        private final int depth;
        private final PlayerColor color;
        private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
        private final ForkJoinPool forkJoinPool;

        public ExpectimaxValue(
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
            return expectimax(board, depth, color);
        }

        private double expectimax(final GameBoard board, final int depth, final PlayerColor currentColor)
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
            if (currentColor == color) {
                final List<ExpectimaxValue> subtasks = new ArrayList<>();
                for (final Point move : availableMoves) {
                    final GameBoard copy = new ArrayBoard(board);
                    BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
                    final ExpectimaxValue val =
                            new ExpectimaxValue(forkJoinPool, board, depth - 1, Utils.reverse(color), utility);
                    val.fork();
                    subtasks.add(val);
                }
                return subtasks.stream().mapToDouble(ForkJoinTask::join).max().orElse(Integer.MIN_VALUE);
            }

            double win = 0;
            final List<ExpectimaxValue> subtasks = new ArrayList<>();
            for (final Point move : availableMoves) {
                final GameBoard copy = new ArrayBoard(board);
                BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
                final ExpectimaxValue val =
                        new ExpectimaxValue(forkJoinPool, board, depth - 1, Utils.reverse(color), utility);
                val.fork();
                subtasks.add(val);
            }
            for (final ExpectimaxValue task : subtasks) {
                win += task.join();
            }
            return win / availableMoves.size();
        }
    }

    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private final ForkJoinPool forkJoinPool;
    private PlayerColor color;

    /**
     * Expectimax c ForkJoinPool.
     * @param depth глубина
     * @param utility функция полезности
     */
    public MTExpectimaxStrategy(final int depth, final ToDoubleBiFunction<GameBoard, PlayerColor> utility) {
        this.depth = depth;
        this.utility = utility;
        forkJoinPool = new ForkJoinPool(6);
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = BoardLogic.getAvailableMoves(board, color);
        double maxWin = Integer.MIN_VALUE;
        Point maxMove = moves.get(0);
        final List<ExpectimaxValue> subtasks = new ArrayList<>();
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final ExpectimaxValue val = new ExpectimaxValue(forkJoinPool, board, depth, color, utility);
            val.fork();
            subtasks.add(val);
        }
        for (int  i = 0; i < subtasks.size(); i++) {
            final double win = subtasks.get(i).join();
            if (win > maxWin) {
                maxWin = win;
                maxMove = moves.get(i);
            }
        }
        return maxMove;
    }

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
    }
}
