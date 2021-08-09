package models.player;

import exception.ServerException;
import logic.BoardLogic;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.List;
import java.util.Random;

public class RandomBotPlayer extends User {

    public RandomBotPlayer(final int id, final String nickname) {
        super(id, nickname);
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        List<Point> points = BoardLogic.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}
