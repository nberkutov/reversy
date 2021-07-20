package models.player;

import exception.GameException;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;
import java.util.Random;

@NoArgsConstructor
public class RandomBot extends Player {

    public RandomBot(final int id) {
        super(id);
    }

    public RandomBot(PlayerColor color) {
        super(color);
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        List<Point> points = BoardService.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}
