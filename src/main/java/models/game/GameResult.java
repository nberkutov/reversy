package models.game;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.board.Board;
import models.base.GameResultState;
import models.player.Player;

@Slf4j
@Getter
@EqualsAndHashCode
public class GameResult {
    private final Board board;
    private final GameResultState resultState;
    private final Player winner;

    public GameResult(final Board board, final GameResultState resultState, final Player winner) {
        this.resultState = resultState;
        this.winner = winner;
        this.board = board;
    }

    private GameResult(final Board board, final GameResultState resultState) {
        this(board, resultState, null);
    }

    public static GameResult winner(final Board board, final Player player) {
        return new GameResult(board, GameResultState.WINNER_FOUND, player);
    }

    public static GameResult draw(final Board board) {
        return new GameResult(board, GameResultState.DRAW);
    }

    public static GameResult playing(final Board board) {
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
