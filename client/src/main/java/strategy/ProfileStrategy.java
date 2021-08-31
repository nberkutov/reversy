package strategy;

import exception.ServerException;
import logic.BoardLogic;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import parser.LogParser;
import profile.Profile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;

public class ProfileStrategy implements Strategy {
    //private  static final String LOG_FILE = "/home/nikita/client/commonLog.log";
    private final int depth;
    private final ToDoubleBiFunction<GameBoard, PlayerColor> utility;
    private final Profile profile;
    private PlayerColor color;

    public ProfileStrategy(final int depth, final Profile profile, final ToDoubleBiFunction<GameBoard, PlayerColor> utility) {
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
            final double win = expectimax(boardCopy, depth, revert(color));
            if (win > maxWin) {
                maxWin = win;
                maxMove = move;
            }
        }

        return maxMove;
    }

    private double expectimax(final GameBoard board, final int depth, final PlayerColor currentColor) throws ServerException {
        final PlayerColor simColor;
        final ToDoubleBiFunction<GameBoard, PlayerColor> estimateFunc;
        if (currentColor == color) {
            simColor = color;
        } else {
            simColor = revert(color);
        }
        estimateFunc = utility;

        final PlayerColor winner = getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return estimateFunc.applyAsDouble(board, simColor);
        }

        final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, simColor);
        if (simColor == color) {
            double maxWin = Integer.MIN_VALUE;
            for (final Point move : availableMoves) {
                final GameBoard copy = new ArrayBoard(board);
                BoardLogic.makeMove(copy, move, Cell.valueOf(simColor));
                final double win = expectimax(copy, depth - 1, revert(simColor));
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
            BoardLogic.makeMove(copy, move, Cell.valueOf(simColor));
            int p = profile.getFrequency(board.toString(), copy.toString());
            if (p == 0) p = 1;
            x += p;
            final double win = expectimax(copy, depth - 1, revert(simColor));
            maxWin += win * profile.getFrequency(board.toString(), copy.toString());
        }
        /*if (x == 0) {
            x = availableMoves.size();
        }*/
        return maxWin / x;
    }

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
        profile.setOpponentState(revert(color));
    }
}
