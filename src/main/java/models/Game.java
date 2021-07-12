package models;

import lombok.Data;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.base.GameState;
import models.base.PlayerColor;

import java.util.Random;

@Data
@Slf4j
@AllArgsConstructor
public class Game {
    private GameState state;
    private final Player blackPlayer;
    private final Player whitePlayer;
    private final Board board;

    private GameResult result;

    public Game(Player first, Player second) throws GameException {
        this(new Board(),first,second);
    }

    public Game(Board board, Player first, Player second) throws GameException {
        state = GameState.BLACK;
        result = GameResult.playing(board);
        if (new Random().nextBoolean()) {
            this.blackPlayer = first;
            this.whitePlayer = second;
        } else {
            this.blackPlayer = second;
            this.whitePlayer = first;
        }
        blackPlayer.setColor(PlayerColor.BLACK);
        whitePlayer.setColor(PlayerColor.WHITE);
        this.board = board;
    }

    public boolean isFinished() {
        return state == GameState.END;
    }


}
