package models.player;

import exception.GameException;
import models.board.Board;
import models.board.Point;
import models.game.Game;
import services.BoardService;

import java.util.List;
import java.util.Random;

public class RandomBot extends Player {

    public RandomBot(final int id) {
        super(id);
    }

    @Override
    public void nextMove(final Game game) throws GameException {
        Board board = game.getBoard();
        List<Point> points = BoardService.getAvailableMoves(board, color);
        Point move = points.get(new Random().nextInt(points.size()));
        BoardService.makeMove(game, move, color);
    }

}
