package models;

import services.BoardService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.base.GameResultState;

@Slf4j
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

    public static GameResult winner(Board board, Player player) {
        return new GameResult(board, GameResultState.WINNER_FOUND, player);
    }

    public static GameResult draw(Board board) {
        return new GameResult(board, GameResultState.DRAW);
    }

    public static GameResult playing(Board board) {
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
