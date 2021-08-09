package models.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.Player;
import models.base.interfaces.GameBoard;


@Data
@Slf4j
@AllArgsConstructor
public class Game {
    private final Player blackUser;
    private final Player whiteUser;
    private final GameBoard board;
    private GameState state;

    public Game(final GameBoard board, final Player first, final Player second) {
        state = GameState.BLACK_MOVE;
        this.blackUser = first;
        this.whiteUser = second;
        blackUser.setColor(PlayerColor.BLACK);
        whiteUser.setColor(PlayerColor.WHITE);
        this.board = board;
    }


}
