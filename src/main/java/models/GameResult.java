package models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GameResult {
    private final Board board;
    private GameResultState resultState;
    private Player winner;

    public GameResult(Board board, GameResultState resultState, Player winner) {
        this.resultState = resultState;
        this.winner = winner;
        this.board = board;
    }

    private GameResult(Board board, GameResultState resultState) {
        this(board, resultState, null);
    }

    public static GameResult draw(Board board) {
        return new GameResult(board, GameResultState.DRAW);
    }

    public static GameResult none(Board board) {
        return new GameResult(board, GameResultState.NONE);
    }
}
