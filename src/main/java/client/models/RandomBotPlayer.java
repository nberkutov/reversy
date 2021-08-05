package client.models;

import exception.GameException;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;
import java.util.Random;

public class RandomBotPlayer extends Player {
    public RandomBotPlayer(String nickname) {
        super(nickname);
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        List<Point> points = BoardService.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }

    @Override
    public void triggerAfterGameEnd(GameState state, GameBoard board) {

    }
}
