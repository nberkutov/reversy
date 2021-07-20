package models.game;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.base.GameResultState;
import models.base.interfaces.GameBoard;
import models.player.Player;

import java.io.Serializable;

@Slf4j
@Getter
@EqualsAndHashCode
public class GameResult implements Serializable {
    private final GameBoard board;
    private final GameResultState resultState;
    private final Player winner;

    public GameResult(final GameBoard board, final GameResultState resultState, final Player winner) {
        this.resultState = resultState;
        this.winner = winner;
        this.board = board;
    }

    private GameResult(final GameBoard board, final GameResultState resultState) {
        this(board, resultState, null);
    }

    public static GameResult winner(final GameBoard board, final Player player) {
        return new GameResult(board, GameResultState.WINNER_FOUND, player);
    }

    public static GameResult playing(final GameBoard board) {
        return new GameResult(board, GameResultState.PLAYING);
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "resultState=" + resultState +
                ", winner=" + winner +
                '}';
    }
}
