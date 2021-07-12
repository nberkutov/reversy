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
        game = new Game(first, second);
    }

    public GameResult play() throws GameException {
        while (!game.isFinished()) {
            log.debug("DEBUG playing \n{}", game.getBoard().getVisualString());
            GameService.doGame(game);
        }

        log.debug("DEBUG finish \n{}", game.getResult().getBoard().getVisualString());
        return game.getResult();
    }
}
