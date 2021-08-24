package strategy;

import exception.ServerException;
import logic.BoardLogic;
import lombok.SneakyThrows;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;

public class MTPruningStragegy implements Strategy {
    private static final class MinimaxValue extends RecursiveTask<Double> {
        private final GameBoard board;
        private final int depth;
        private final PlayerColor color;
        private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
        private double alpha;
        private double beta;

        public MinimaxValue(
                            final GameBoard board, final int depth,
                            final PlayerColor color, final ToDoubleBiFunction<GameBoard, PlayerColor> utility, final double alpha, final double beta) {
            this.board = board;
            this.depth = depth;
            this.color = color;
            this.utility = utility;
            this.alpha = alpha;
            this.beta = beta;
        }

        @SneakyThrows
        @Override
        protected Double compute() {
            return minimax(board, depth, color, alpha, beta);
        }

        private double minimax(final GameBoard board, final int depth, final PlayerColor currentColor, final double alpha, final double beta) throws ServerException {
            final PlayerColor simColor;
            final ToDoubleBiFunction<GameBoard, PlayerColor> estimateFunc;
            final boolean maximizingPlayer;
            if (currentColor == color) {
                maximizingPlayer = true;
                estimateFunc = utility;
            } else {
                maximizingPlayer = false;
                estimateFunc = (b, c) -> -utility.applyAsDouble(b, c);
            }
            final PlayerColor winner = getEndOfGame(board);
            if (depth == 0 || winner != PlayerColor.NONE) {
                return estimateFunc.applyAsDouble(board, currentColor);
            }
            final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, currentColor);
            //final List<MinimaxValue> subtasks = new ArrayList<>();
            double maxWin = Integer.MIN_VALUE;

            for (final Point move : availableMoves) {
                final GameBoard copy = new ArrayBoard(board);
                ClientBoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
                final MinimaxValue minimaxValue = new MinimaxValue(board, depth - 1, revert(currentColor), utility, alpha, beta);
                final double win = minimaxValue.fork().join();
                if (maximizingPlayer) {
                    if (win > beta) {
                        break;
                    }
                    this.alpha = Math.max(win, alpha);
                } else {
                    if (win < alpha) {
                        break;
                    }
                    this.beta = Math.min(win, beta);
                }
                if (win > maxWin) {
                    maxWin = win;
                }
            /*if (maximizingPlayer) {
                alpha = Math.max(win, alpha);
            } else {
                beta = Math.min(win, beta);
            }*/
            }
            return maxWin;
        }

        private PlayerColor revert(final PlayerColor color) {
            switch (color) {
                case WHITE:
                    return PlayerColor.BLACK;
                case BLACK:
                    return PlayerColor.WHITE;
                case NONE:
                    return PlayerColor.NONE;
            }
            return PlayerColor.NONE;
        }

        private PlayerColor getEndOfGame(final GameBoard board) throws ServerException {
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

    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private final ForkJoinPool forkJoinPool;
    private PlayerColor color;


    public MTPruningStragegy(final int depth, final ToDoubleBiFunction<GameBoard, PlayerColor> utility) {
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
        double maxWin = Integer.MIN_VALUE;
        Point maxMove = null;
        final List<MinimaxValue> subtasks = new ArrayList<>();
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final MinimaxValue val = new MinimaxValue(boardCopy, depth, revert(color), utility, Integer.MIN_VALUE, Integer.MAX_VALUE);
            final double win = val.fork().join();
            if (win > maxWin) {
                maxWin = win;
                maxMove = move;
            }
            //subtasks.add(val);
        }
        /*for (final MinimaxValue task : subtasks) {
            final double win = task.join();
            if (win > maxWin) {
                maxWin = win;
                maxMove = moves.get(subtasks.indexOf(task));
            }
        }*/
        if (maxMove == null) {
            return moves.get(0);
        }
        return maxMove;
    }
}