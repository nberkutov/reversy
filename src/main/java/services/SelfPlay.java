package services;

import exception.GameException;

import lombok.extern.slf4j.Slf4j;
import models.board.Board;
import models.game.Game;
import models.game.GameResult;
import models.player.Player;

@Slf4j
public class SelfPlay {
    private final Player first;
    private final Player second;
    private final Game game;

    public SelfPlay(final Player first, final Player second) {
        this.first = first;
        this.second = second;
        Board board = new Board();
        game = new Game(board, first, second);
    }

    public GameResult play() throws GameException {
//        log.debug("START PLAYING ");
        while (!game.isFinished()) {
//            log.info("Board State\n{}", game.getBoard().getVisualString());
            GameService.playNext(game);
        }

        log.debug("DEBUG finish \n{}", game.getResult().getBoard().getVisualString());
        return GameService.getGameResult(game);
    }
}
