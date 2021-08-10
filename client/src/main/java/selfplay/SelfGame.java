package selfplay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.Player;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;


@Data
@Slf4j
@AllArgsConstructor
public class SelfGame {
    private final Player blackPlayer;
    private final Player whitePlayer;
    private final GameBoard board;
    private GameState state;

    public SelfGame(final GameBoard board, final Player first, final Player second) {
        state = GameState.BLACK_MOVE;
        this.blackPlayer = first;
        this.whitePlayer = second;
        blackPlayer.setColor(PlayerColor.BLACK);
        whitePlayer.setColor(PlayerColor.WHITE);
        this.board = board;
    }


}
