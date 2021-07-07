package services;

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

    public SelfPlay(Player first, Player second) {
        this.first = first;
        this.second = second;
        game = new Game(first, second);
        try {
            System.out.println(game.getBoard().getVisualString());
        } catch (GameException e) {
            e.printStackTrace();
        }
    }

    public void play() throws GameException {
        while (!game.isFinished()) {
            game.next();
            Board board = game.getBoard();
        }
        GameResult result = game.getResult();
    }

    public static void main(String[] args) {
        SelfPlay selfPlay = new SelfPlay(new Player(), new Player());
    }
}
