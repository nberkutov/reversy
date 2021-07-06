package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.base.GameState;

@Data
@Slf4j
@AllArgsConstructor
public class Game {
    private GameState state;
    private final Board board;
    private final Player black;
    private final Player white;

    private int countBlack;
    private int countWhite;

    public Game(Player black, Player white) {
        board = new Board();
        this.black = black;
        this.white = white;
        state = GameState.BLACK;
    }

    public boolean isFinished() {
        return state == GameState.END;
    }

    public void next() {
        switch (state) {
            case BLACK:
                black.nextMove(board);
                state = GameState.WHITE;
                break;
            case WHITE:
                white.nextMove(board);
                state = GameState.BLACK;
        }
    }
}
