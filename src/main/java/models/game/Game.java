package models.game;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.board.Board;
import models.base.GameState;
import models.base.PlayerColor;
import models.player.Player;

import java.util.Random;

@Data
@Slf4j
@AllArgsConstructor
public class Game {
    private int id;
    private final Player blackPlayer;
    private final Player whitePlayer;
    private final Board board;

    private GameState state;
    private GameResult result;

    public Game(final int id, final Player first, final Player second) {
        this(first, second);
        this.id = id;
    }

    public Game(final Player first, final Player second) {
        this(new Board(), first, second);
    }

    public Game(final Board board, final Player first, final Player second) {
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
