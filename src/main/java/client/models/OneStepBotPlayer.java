package client.models;

import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;
import services.BoardService;

import java.util.List;

public class OneStepBotPlayer extends Player {

    public OneStepBotPlayer(String nickname) {
        super(nickname);
    }

    @Override
    public Point move(GameBoard board) throws GameException {
        List<Point> availableMoves = BoardService.getAvailableMoves(board, color);
        int max = 0;
        Point maxMove = availableMoves.get(0);
        for (Point move : availableMoves) {
            GameBoard gameBoard = new Board(board);
            BoardService.makeMove(gameBoard, move, Cell.valueOf(color));
            int usefulness = getUsefulness(board);
            if (usefulness > max) {
                max = usefulness;
                maxMove = move;
            }
        }
        return maxMove;
    }

    int getUsefulness(GameBoard board) {
        if (color == PlayerColor.BLACK) {
            return board.getCountBlackCells();
        }
        return board.getCountWhiteCells();
    }
}
