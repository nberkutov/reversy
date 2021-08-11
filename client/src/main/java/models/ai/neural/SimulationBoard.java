package models.ai.neural;

import exception.ServerException;
import logic.BoardLogic;
import lombok.Data;
import models.GameProperties;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.Arrays;
import java.util.List;

@Data
public class SimulationBoard {
    private GameBoard board;
    private PlayerColor moveColor;

    public SimulationBoard(GameBoard board, PlayerColor moveColor) {
        this.board = board.clone();
        this.moveColor = moveColor;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws ServerException {
        return !BoardLogic.getAvailableMoves(board, color).isEmpty();
    }

    public void move(Point p) throws ServerException {
        BoardLogic.makeMove(board, p, Cell.valueOf(moveColor));
        if (canMove(board, moveColor.getOpponent())) {
            moveColor = moveColor.getOpponent();
        }
    }

    public List<Point> getCanMoves() throws ServerException {
        return BoardLogic.getAvailableMoves(board, moveColor);
    }

    public boolean isGameEnd() throws ServerException {
        return BoardLogic.isNotPossiblePlayOnBoard(board);
    }

    public double getScore(PlayerColor myColor) throws ServerException {
        if (isPlayerLose(board, myColor)) {
            return 0;
        }
        return (board.getCountEmpty() + 1) * BoardLogic.getCountCellByPlayerColor(board, myColor) * (countCorners(board, myColor) + 1);
    }

    private int countCorners(final GameBoard board, final PlayerColor color) throws ServerException {
        int count = 0;
        for (final Point p : cornerPoints()) {
            if (board.getCell(p) == Cell.valueOf(color)) {
                count++;
            }
        }
        return count;
    }

    private List<Point> cornerPoints() {
        return Arrays.asList(
                new Point(0, 0),
                new Point(0, GameProperties.BOARD_SIZE - 1),
                new Point(GameProperties.BOARD_SIZE - 1, 0),
                new Point(GameProperties.BOARD_SIZE - 1, GameProperties.BOARD_SIZE - 1));
    }

    private boolean isPlayerLose(GameBoard board, PlayerColor color) {
        if (color == PlayerColor.BLACK && board.getCountBlackCells() == 0) {
            return true;
        }
        if (color == PlayerColor.WHITE && board.getCountWhiteCells() == 0) {
            return true;
        }
        if (board.getCountEmpty() == 0) {
            if (color == PlayerColor.BLACK && board.getCountBlackCells() <= board.getCountWhiteCells()) {
                return true;
            }
            return color == PlayerColor.WHITE && board.getCountWhiteCells() < board.getCountBlackCells();
        }
        return false;
    }
}
