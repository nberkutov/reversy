package models;

import controller.BoardController;
import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.base.GameState;
import models.base.PlayerColor;

import java.util.Random;

@Getter
@Slf4j
@AllArgsConstructor
public class Game {
    private GameState state;
    private BoardController boardController;
    private final Player black;
    private final Player white;

    private int countBlack;
    private int countWhite;

    private GameResult result;

    public Game(BoardController boardController, Player first, Player second) throws GameException {
        state = GameState.BLACK;
        this.boardController = boardController;
        result = GameResult.playing(boardController);
        if (new Random().nextBoolean()) {
            this.black = first;
            this.white = second;
        } else {
            this.black = second;
            this.white = first;
        }
        black.setBoardController(boardController);
        white.setBoardController(boardController);
        black.setColor(PlayerColor.BLACK);
        white.setColor(PlayerColor.WHITE);
    }

    public boolean isFinished() {
        return state == GameState.END;
    }

    public void next() throws GameException {
        switch (state) {
            case BLACK:
                if (boardController.isPossibleMove(black)) {
                    black.nextMove();
                }
                state = GameState.WHITE;
                break;
            case WHITE:
                if (boardController.isPossibleMove(white)) {
                    white.nextMove();
                }
                state = GameState.BLACK;
                break;
            case END:
                break;
        }
        if (isEndGame()) {
            state = GameState.END;
        }
    }

    private boolean isEndGame() throws GameException {
        return boardController.getCountEmpty() == 0 ||
                (!boardController.isPossibleMove(black)
                        && boardController.isPossibleMove(white));
    }

    public GameResult getResult() throws GameException {
        if (state != GameState.END) {
            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        }
        long blackCells = boardController.getCountBlack();
        long whiteCells = boardController.getCountWhite();
        if (blackCells == whiteCells) {
            return GameResult.draw(boardController);
        } else if (blackCells > whiteCells) {
            return GameResult.winner(boardController, black);
        } else {
            return GameResult.winner(boardController, white);
        }
    }
}
