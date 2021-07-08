package services;

import exception.GameException;

import lombok.extern.slf4j.Slf4j;
import models.*;

@Slf4j
public class SelfPlay {
    private final Player first;
    private final Player second;
    private final Game game;
    private final BoardGUI boardGUI;

    public SelfPlay(Player first, Player second) throws GameException {
        this.first = first;
        this.second = second;
        Board board = new Board();
        MoveService moveService = new MoveService(board);
        game = new Game(moveService, first, second);
        boardGUI = new BoardGUI(moveService.getBoard());
    }

    public GameResult play() throws GameException {
        while (!game.isFinished()) {
            log.debug("DEBUG playing \n{}", boardGUI.getStringRepresentation());
            game.next();
        }

        log.debug("DEBUG finish \n{}", boardGUI.getStringRepresentation());
        return game.getResult();
    }
}
