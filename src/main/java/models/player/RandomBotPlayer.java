package models.player;

import exception.GameException;
import lombok.NoArgsConstructor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;
import java.util.Random;

public class RandomBotPlayer extends Player {

    public RandomBotPlayer(final int id, final String nickname) {
        super(id, nickname);
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        List<Point> points = BoardService.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}