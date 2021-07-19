package models.player;

import exception.GameException;
import models.base.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;
import java.util.Random;

public class RandomBot extends Player {

    public RandomBot(final int id) {
        super(id);
    }

    @Override
    public Point move(GameBoard board) throws GameException {
        List<Point> points = BoardService.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}
