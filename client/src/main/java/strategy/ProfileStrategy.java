package strategy;

import base.Strategy;
import exception.ServerException;
import logic.BoardLogic;
import models.ArrayBoard;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import profile.Profile;
import utils.Utils;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class ProfileStrategy implements Strategy {
    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private final Profile profile;
    private PlayerColor color;

    /**
     * Экспектимакс с использованием профиля.
     * @param depth максимальная глубина дерева.
     * @param profile профиль игрока.
     * @param utility функция полезности.
     */
    public ProfileStrategy(
            final int depth, final Profile profile, final ToDoubleBiFunction<GameBoard, PlayerColor> utility) {
        this.depth = depth;
        this.utility = utility;
        this.profile = profile;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = BoardLogic.getAvailableMoves(board, color);
        double maxWin = Integer.MIN_VALUE;
        Point maxMove = moves.get(0);
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final double win = expectimax(boardCopy, depth, Utils.reverse(color));
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

        final PlayerColor winner = Utils.getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return estimateFunc.applyAsDouble(board, currentColor);
        }

        final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, currentColor);
        if (currentColor == color) {
            double maxWin = Integer.MIN_VALUE;
            for (final Point move : availableMoves) {
                final GameBoard copy = new ArrayBoard(board);
                BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
                final double win = expectimax(copy, depth - 1, Utils.reverse(currentColor));
                if (win > maxWin) {
                    maxWin = win;
                }
            }
            return maxWin;
        }

        double maxWin = 0;
        double x = 0;
        for (final Point move : availableMoves) {
            final GameBoard copy = new ArrayBoard(board);
            BoardLogic.makeMove(copy, move, Cell.valueOf(currentColor));
            int frequency = profile.getFrequency(board.toString(), copy.toString());
            if (frequency == 0) {
                frequency = 1;
            }
            x += frequency;
            final double win = expectimax(copy, depth - 1, Utils.reverse(currentColor)) * frequency;
            maxWin += win;
        }
        return maxWin / x;
    }

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
        profile.setOpponentState(Utils.reverse(color));
    }
}
