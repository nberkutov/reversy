package client.models.ai.neural;

import exception.GameException;
import lombok.Data;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;

@Data
public class SimulationBoard {
    private GameBoard board;
    private PlayerColor moveColor;

    public SimulationBoard(GameBoard board, PlayerColor moveColor) {
        this.board = board.clone();
        this.moveColor = moveColor;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws GameException {
        return !BoardService.getAvailableMoves(board, color).isEmpty();
    }

    public void move(Point p) throws GameException {
        BoardService.makeMove(board, p, Cell.valueOf(moveColor));
        if (canMove(board, moveColor.getOpponent())) {
            moveColor = moveColor.getOpponent();
        }
    }

    public List<Point> getCanMoves() throws GameException {
        return BoardService.getAvailableMoves(board, moveColor);
    }

    public boolean isGameEnd() throws GameException {
        return BoardService.isNotPossiblePlayOnBoard(board);
    }

    public double getScore(PlayerColor myColor) throws GameException {
        if (isPlayerLose(board, myColor)) {
            return 0;
        }
        return (board.getCountEmpty() + 1) * BoardService.getCountCellByPlayerColor(board, myColor);
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
