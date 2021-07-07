package services;

import controller.BoardController;
import exception.GameException;

import lombok.extern.slf4j.Slf4j;
import models.Board;
import models.Game;
import models.GameResult;
import models.Player;

@Slf4j
public class SelfPlay {
    private final Player first;
    private final Player second;
    private final Game game;

    public SelfPlay(Player first, Player second) throws GameException {
        this.first = first;
        this.second = second;
        Board board = new Board();
        BoardController boardController = new BoardController(board);
        game = new Game(boardController, first, second);
    }

    public GameResult play() throws GameException {
        while (!game.isFinished()) {
            log.debug("DEBUG playing \n{}", game.getBoardController().getBoard().getVisualString());
            game.next();
        }

        log.debug("DEBUG finish \n{}", game.getResult().getBoard().getVisualString());
        return game.getResult();
    }

    public static void main(String[] args) throws GameException {
        SelfPlay selfPlay = new SelfPlay(new Player(), new Player());
    }
}
