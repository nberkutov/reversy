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

    private GameResultState resultState;
    private Player winner;

    public GameResult(GameResultState resultState, Player winner) {
        this.resultState = resultState;
        this.winner = winner;
    }

    private GameResult(GameResultState resultState) {
        this(resultState, null);
    }

    public static GameResult winner(BoardService boardService, Player player) {
        return new GameResult(GameResultState.WINNER_FOUND, player);
    }

    public static GameResult draw(BoardService boardService) {
        return new GameResult(GameResultState.DRAW);
    }

    public static GameResult playing(BoardService boardService) {
        return new GameResult(GameResultState.PLAYING);
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "resultState=" + resultState +
                ", winner=" + winner +
                '}';
    }
}
