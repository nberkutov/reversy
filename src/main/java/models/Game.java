package models;

import services.MoveService;
import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.base.GameState;

import java.util.Random;

@Getter
@Slf4j
@AllArgsConstructor
public class Game {
    private GameState state;
    private MoveService moveService;
    private final Player black;
    private final Player white;

    private int countBlack;
    private int countWhite;

    private GameResult result;

    public Game(MoveService moveService, Player first, Player second) throws GameException {
        state = GameState.BLACK;
        this.moveService = new MoveService(new Board());
        result = GameResult.playing(moveService.getBoard());
        if (new Random().nextBoolean()) {
            this.black = first;
            this.white = second;
        } else {
            this.black = second;
            this.white = first;
        }
    }

    public boolean isFinished() {
        return state == GameState.END;
    }

    public void next() throws GameException {
        switch (state) {
            case BLACK:
                if (moveService.isPossibleMove(black)) {
                    black.nextMove();
                }
                state = GameState.WHITE;
                break;
            case WHITE:
                if (moveService.isPossibleMove(white)) {
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
        return moveService.getCountEmpty() == 0 ||
                (!moveService.isPossibleMove(black)
                        && moveService.isPossibleMove(white));
    }

    public GameResult getResult() throws GameException {
        if (state != GameState.END) {
            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        }
        long blackCells = moveService.getCountBlack();
        long whiteCells = moveService.getCountWhite();
        if (blackCells == whiteCells) {
            return GameResult.draw(moveService.getBoard());
        } else if (blackCells > whiteCells) {
            return GameResult.winner(moveService.getBoard(), black);
        } else {
            return GameResult.winner(moveService.getBoard(), white);
        }
    }
}
