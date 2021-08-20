package strategy;

import exception.ServerException;
import logic.BoardLogic;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.List;
import java.util.function.ToIntBiFunction;

public class ABPruningStrategy implements Strategy {
    private final int depth;
    private final ToIntBiFunction<GameBoard, PlayerColor> utility;
    private PlayerColor color;

    public ABPruningStrategy(final int depth, final ToIntBiFunction<GameBoard, PlayerColor> utility){
        this.depth = depth;
        this.utility = utility;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = BoardLogic.getAvailableMoves(board, color);
        int maxWin = Integer.MIN_VALUE;
        Point maxMove = null;
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final int win = minimax(boardCopy, depth, revert(color), Integer.MIN_VALUE, Integer.MAX_VALUE);
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

    private int minimax(final GameBoard board, final int depth, final PlayerColor currentColor, int alpha, int beta)
            throws ServerException {
        final PlayerColor simColor;
        final ToIntBiFunction<GameBoard, PlayerColor> estimateFunc;
        final boolean maximizingPlayer;
        if (currentColor == color) {
            simColor = color;
            estimateFunc =  utility;
            maximizingPlayer = true;
        } else {
            simColor = revert(color);
            estimateFunc = utility;
            maximizingPlayer = false;
        }

        final PlayerColor winner = getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return estimateFunc.applyAsInt(board, simColor);
        }
        final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, simColor);
        int maxWin = Integer.MIN_VALUE;
        for (final Point move : availableMoves) {
            final GameBoard copy = new ArrayBoard(board);
            BoardLogic.makeMove(copy, move, Cell.valueOf(simColor));
            final int win = minimax(copy, depth - 1, revert(currentColor), alpha, beta);
            if (maximizingPlayer) {
                if (win > beta) {
                    break;
                }
            } else {
                if (win < alpha) {
                    break;
                }
            }
            if (win > maxWin) {
                maxWin = win;
            }
            if (maximizingPlayer) {
                alpha = Math.max(win, alpha);
            } else {
                beta = Math.min(win, beta);
            }
        }
        return maxWin;
    }

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
    }
}
