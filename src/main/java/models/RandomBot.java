package models;

import exception.GameException;
import services.BoardService;

import java.util.List;
import java.util.Random;

public class RandomBot extends Player {

    public RandomBot(final int id) {
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
