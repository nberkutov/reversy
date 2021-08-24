package strategy;

import exception.ServerException;
import logic.BoardLogic;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.List;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;

public class ABPruningStrategy implements Strategy {
    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private PlayerColor color;

    public ABPruningStrategy(final int depth, final ToDoubleBiFunction<GameBoard, PlayerColor> utility){
        this.depth = depth;
        this.utility = utility;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = ClientBoardLogic.getAvailableMoves(board, color);
        double maxWin = Integer.MIN_VALUE;
        Point maxMove = null;
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            ClientBoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final double win = minimax(boardCopy, depth, revert(color), Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (win > maxWin) {
                maxWin = win;
                maxMove = move;
            }
        }
        if (maxMove == null) {
            return moves.get(0);
        }
        return maxMove;
    }

    private double minimax(final GameBoard board, final int depth, final PlayerColor currentColor, double alpha, double beta)
            throws ServerException {
        final PlayerColor simColor;
        final ToDoubleBiFunction<GameBoard, PlayerColor> estimateFunc;
        final boolean maximizingPlayer;
        if (currentColor == color) {
            simColor = color;
            estimateFunc =  utility;
            maximizingPlayer = true;
        } else {
            simColor = revert(color);
            estimateFunc = (b, c) -> -utility.applyAsDouble(b, c);
            maximizingPlayer = false;
        }

        final PlayerColor winner = getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return estimateFunc.applyAsDouble(board, simColor);
        }
        final List<Point> availableMoves = ClientBoardLogic.getAvailableMoves(board, simColor);
        double maxWin = Integer.MIN_VALUE;
        for (final Point move : availableMoves) {
            final GameBoard copy = new ArrayBoard(board);
            ClientBoardLogic.makeMove(copy, move, Cell.valueOf(simColor));
            final double win = minimax(copy, depth - 1, revert(currentColor), alpha, beta);
            if (maximizingPlayer) {
                if (win > beta) {
                    break;
                }
                alpha = Math.max(win, alpha);
            } else {
                if (win < alpha) {
                    break;
                }
                beta = Math.min(win, beta);
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

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
    }
}
