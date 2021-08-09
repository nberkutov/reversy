package models.game;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.base.GameResultState;
import models.base.interfaces.GameBoard;
import models.player.User;

import java.io.Serializable;
import java.util.Objects;

@Slf4j
@Getter
public class GameResult implements Serializable, Comparable {
    private final GameBoard board;
    private final GameResultState resultState;
    private final User winner;
    private final User loser;

    public GameResult(final GameBoard board, final GameResultState resultState, final User winner, final User loser) {
        this.resultState = resultState;
        this.winner = winner;
        this.board = board;
        this.loser = loser;
    }

    private GameResult(final GameBoard board, final GameResultState resultState) {
        this(board, resultState, null, null);
    }

    public static GameResult winner(final GameBoard board, final User winner, final User loser) {
        return new GameResult(board, GameResultState.ORDINARY_VICTORY, winner, loser);
    }

    public static GameResult techWinner(final Game game, final User loser) {
        if (game.getBlackUser().equals(loser)) {
            return new GameResult(game.getBoard(), GameResultState.TECHNICAL_VICTORY, game.getWhiteUser(), game.getWhiteUser());
        }
        return new GameResult(game.getBoard(), GameResultState.TECHNICAL_VICTORY, game.getBlackUser(), game.getWhiteUser());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResult that = (GameResult) o;
        return Objects.equals(board, that.board) &&
                resultState == that.resultState &&
                Objects.equals(winner, that.winner) &&
                Objects.equals(loser, that.loser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, resultState, winner, loser);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
