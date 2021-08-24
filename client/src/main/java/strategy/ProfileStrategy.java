package strategy;

import exception.ServerException;
import logic.BoardLogic;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.ArrayBoard;
import models.board.Point;
import parser.LogParser;
import profile.Profile;

import java.util.List;
import java.util.function.ToIntBiFunction;

public class ProfileStrategy implements Strategy {
    private final int depth;
    private final ToIntBiFunction<GameBoard, PlayerColor> utility;
    private final Profile profile;
    private PlayerColor color;

    public ProfileStrategy(final int depth, final ToIntBiFunction<GameBoard, PlayerColor> utility) {
        this.depth = depth;
        this.utility = utility;
        profile = new LogParser().parse();
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> moves = BoardLogic.getAvailableMoves(board, color);
        int maxWin = Integer.MIN_VALUE;
        Point maxMove = null;
        for (final Point move : moves) {
            final GameBoard boardCopy = new ArrayBoard(board);
            BoardLogic.makeMove(boardCopy, move, Cell.valueOf(color));
            final int win = expectimax(boardCopy, depth, revert(color));
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

    private int expectimax(final GameBoard board, final int depth, final PlayerColor currentColor) throws ServerException {
        final PlayerColor simColor;
        final ToIntBiFunction<GameBoard, PlayerColor> estimateFunc;
        if (currentColor == color) {
            simColor = color;
        } else {
            simColor = revert(color);
        }
        estimateFunc = utility;

        final PlayerColor winner = getEndOfGame(board);
        if (depth == 0 || winner != PlayerColor.NONE) {
            return estimateFunc.applyAsInt(board, simColor);
        }
        final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, simColor);
        if (simColor == color) {
            int maxWin = Integer.MIN_VALUE;
            for (final Point move : availableMoves) {
                final GameBoard copy = new ArrayBoard(board);
                BoardLogic.makeMove(copy, move, Cell.valueOf(simColor));
                final int win = expectimax(copy, depth - 1, revert(simColor));
                if (win > maxWin) {
                    maxWin = win;
                }
            }
            return maxWin;
        }
        final int maxWin = 0;
        for (final Point move : availableMoves) {
            final GameBoard copy = new ArrayBoard(board);
            BoardLogic.makeMove(copy, move, Cell.valueOf(simColor));
            final int win = expectimax(copy, depth - 1, revert(simColor)) *
                    profile.getProbability(board.toString(), copy.toString());
        }
        return maxWin;
        //return win / availableMoves.size();
    }

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
        profile.setOpponentState(revert(color));
    }
}
