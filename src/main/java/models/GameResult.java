package models;

import controller.BoardController;
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

    public static GameResult winner(BoardController boardController, Player player) {
        return new GameResult(boardController.getBoard(), GameResultState.WINNER_FOUND, player);
    }

    public static GameResult draw(BoardController boardController) {
        return new GameResult(boardController.getBoard(), GameResultState.DRAW);
    }

    public static GameResult playing(BoardController boardController) {
        return new GameResult(boardController.getBoard(), GameResultState.PLAYING);
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "resultState=" + resultState +
                ", winner=" + winner +
                '}';
    }
}
