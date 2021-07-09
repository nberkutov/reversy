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
    private final Player blackPlayer;
    private final Player whitePlayer;

    private int countBlack;
    private int countWhite;

    public Game(BoardService boardService, Player first, Player second) {
        state = GameState.BLACK;
        this.boardService = boardService;
        if (new Random().nextBoolean()) {
            this.blackPlayer = first;
            this.whitePlayer = second;
        } else {
            this.blackPlayer = second;
            this.whitePlayer = first;
        }
        blackPlayer.setBoardService(boardService);
        whitePlayer.setBoardService(boardService);
        blackPlayer.setColor(PlayerColor.BLACK);
        whitePlayer.setColor(PlayerColor.WHITE);
    }

    public boolean isFinished() {
        return state == GameState.END;
    }

    public void next() throws GameException {
        switch (state) {
            case BLACK:
                if (boardService.isPossibleMove(blackPlayer)) {
                    blackPlayer.nextMove();
                }
                state = GameState.WHITE;
                break;
            case WHITE:
                if (boardService.isPossibleMove(whitePlayer)) {
                    whitePlayer.nextMove();
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
                (!boardService.isPossibleMove(blackPlayer)
                        && boardService.isPossibleMove(whitePlayer));
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
            return GameResult.winner(boardService, blackPlayer);
        } else {
            return GameResult.winner(boardService, whitePlayer);
        }
    }
}
