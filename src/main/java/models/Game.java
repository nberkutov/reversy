package models;

import services.BoardService;
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
    private BoardService boardService;
    private final Player black;
    private final Player white;

    private int countBlack;
    private int countWhite;

    private GameResult result;

    public Game(BoardService boardService, Player first, Player second) throws GameException {
        state = GameState.BLACK;
        this.boardService = boardService;
        result = GameResult.playing(boardService);
        if (new Random().nextBoolean()) {
            this.black = first;
            this.white = second;
        } else {
            this.black = second;
            this.white = first;
        }
        black.setBoardService(boardService);
        white.setBoardService(boardService);
        black.setColor(PlayerColor.BLACK);
        white.setColor(PlayerColor.WHITE);
    }

    public boolean isFinished() {
        return state == GameState.END;
    }

    public void next() throws GameException {
        switch (state) {
            case BLACK:
                if (boardService.isPossibleMove(black)) {
                    black.nextMove();
                }
                state = GameState.WHITE;
                break;
            case WHITE:
                if (boardService.isPossibleMove(white)) {
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
        return boardService.getCountEmpty() == 0 ||
                (!boardService.isPossibleMove(black)
                        && boardService.isPossibleMove(white));
    }

    public GameResult getResult() throws GameException {
        if (state != GameState.END) {
            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        }
        long blackCells = boardService.getCountBlack();
        long whiteCells = boardService.getCountWhite();
        if (blackCells == whiteCells) {
            return GameResult.draw(boardService);
        } else if (blackCells > whiteCells) {
            return GameResult.winner(boardService, black);
        } else {
            return GameResult.winner(boardService, white);
        }
    }
}
