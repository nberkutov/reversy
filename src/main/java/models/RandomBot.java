package models;

import services.BoardService;
import exception.GameException;
import models.base.PlayerColor;

import java.util.*;

public class RandomBot extends Player {

    public RandomBot(long id) {
        super(id);
    }

    public RandomBot(long id, PlayerColor color, BoardService boardService) {
        super(id, color);
    }

    @Override
    public void nextMove(Game game) throws GameException {
        Board board = game.getBoard();
        List<Point> points = BoardService.getAvailableMoves(board, color);
        Point move = points.get(new Random().nextInt(points.size()));
        BoardService.makeMove(game, move, color);
    }

}
