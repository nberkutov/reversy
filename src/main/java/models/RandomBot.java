package models;

import services.BoardService;
import exception.GameException;
import models.base.PlayerColor;

import java.util.*;

public class RandomBot extends Player {

    public RandomBot(final long id) {
        super(id);
    }
/*

    public RandomBot(final long id, final PlayerColor color, final BoardService boardService) {
        super(id, color);
    }

*/
    @Override
    public void nextMove(final Game game) throws GameException {
        Board board = game.getBoard();
        List<Point> points = BoardService.getAvailableMoves(board, color);
        Point move = points.get(new Random().nextInt(points.size()));
        BoardService.makeMove(game, move, color);
    }

}
