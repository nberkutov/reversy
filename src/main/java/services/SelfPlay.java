package services;

import exception.GameException;
import models.Board;
import models.Game;
import models.GameResult;
import models.Player;

public class SelfPlay {
    private final Player first;
    private final Player second;
    private final Game game;

    public SelfPlay(Player first, Player second) {
        this.first = first;
        this.second = second;
        game = new Game(first, second);
    }

    public void play() throws GameException {
        while (!game.isFinished()) {
            game.next();
            Board board = game.getBoard();
        }
        GameResult result = game.getResult();
    }
}
