package strategy;

import logic.BoardLogic;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import exception.ServerException;

import java.util.List;

public interface Strategy {
    Point move(GameBoard board) throws ServerException;
    void setColor(PlayerColor color);

    default PlayerColor getEndOfGame(final GameBoard board) throws ServerException {
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

    default PlayerColor revert(final PlayerColor colorToRevert) {
        if (colorToRevert == PlayerColor.BLACK) {
            return PlayerColor.WHITE;
        }
        return PlayerColor.BLACK;
    }
}
