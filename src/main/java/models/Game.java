package models;

import lombok.Data;
import services.BoardService;
import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.base.GameState;
import models.base.PlayerColor;
import services.GameService;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
@AllArgsConstructor
public class Game {
    private GameState state;
    private final Player black;
    private final Player white;
    private final Board board;

    private GameResult result;

    public Game(Player first, Player second) throws GameException {
        this(new Board(),first,second);
    }

    public Game(Board board, Player first, Player second) throws GameException {
        state = GameState.BLACK;
        result = GameResult.playing(board);
        if (new Random().nextBoolean()) {
            this.black = first;
            this.white = second;
        } else {
            this.black = second;
            this.white = first;
        }
        black.setColor(PlayerColor.BLACK);
        white.setColor(PlayerColor.WHITE);
        this.board = board;
    }

    public boolean isFinished() {
        return state == GameState.END;
    }


}
