package services;

import exception.GameException;

import lombok.extern.slf4j.Slf4j;
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
        game = new Game(board, first, second);
    }

    public GameResult play() throws GameException {
        log.debug("START PLAYING ");
        while (!game.isFinished()) {
            log.info("Board State\n{}", game.getBoard().getVisualString());
            GameService.doGame(game);
        }

        log.debug("DEBUG finish \n{}", game.getResult().getBoard().getVisualString());
        return game.getResult();
    }
}
