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

public class ExpectimaxStrategy implements Strategy {
    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private PlayerColor color;

    public ExpectimaxStrategy(final int depth, final ToDoubleBiFunction<GameBoard, PlayerColor> utility) {
        this.depth = depth;
        this.utility = utility;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = BoardLogic.getAvailableMoves(board, color);
        double maxWin = Integer.MIN_VALUE;
        Point maxMove = moves.get(0);
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final double win = expectimax(boardCopy, depth, revert(color));
            if (win > maxWin) {
                maxWin = win;
                maxMove = move;
            }
        }      
        return maxMove;
    }

    private double expectimax(final GameBoard board, final int depth, final PlayerColor currentColor) throws ServerException {   
        final ToDoubleBiFunction<GameBoard, PlayerColor> estimateFunc;
        if (currentColor == color) {            
            estimateFunc = utility;
        } else {            
            estimateFunc = (b, c) -> -utility.applyAsDouble(b, c);
        }

        final PlayerColor winner = getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return estimateFunc.applyAsDouble(board, currentColor);
        }
        final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, currentColor);
        if (currentColor == color) {
            double maxWin = Integer.MIN_VALUE;
            for (final Point move : availableMoves) {
                final GameBoard copy = new ArrayBoard(board);
                BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
                final double win = expectimax(copy, depth - 1, revert(currentColor));
                if (win > maxWin) {
                    maxWin = win;
                }
            }
            return maxWin;
        }
        double win = 0;
        for (final Point move : availableMoves) {
            final GameBoard copy = new ArrayBoard(board);
            BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
            win += expectimax(copy, depth - 1, revert(currentColor));
        }
        return win / availableMoves.size();
    }

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
    }
}
