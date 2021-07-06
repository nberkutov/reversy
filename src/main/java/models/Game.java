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

    private GameResult result;

    public Game(Player black, Player white) {
        board = new Board();
        this.black = black;
        this.white = white;
        state = GameState.BLACK;
        result = GameResult.none(board);
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
                break;
            case END:
                break;
        }
    }

    public GameResult getResult() throws Exception {
        if (state != GameState.END) {
            throw new Exception("Game is not finished yet.");
        }
        long blackCells = board.getCountCell(Cell.BLACK);
        long whiteCells = board.getCountCell(Cell.WHITE);
        if (blackCells == whiteCells) {
            return GameResult.draw(board);
        } else if (blackCells > whiteCells) {
            return new GameResult(board, GameResultState.BLACK, black);
        } else {
            return new GameResult(board, GameResultState.WHITE, white);
        }
    }
}
