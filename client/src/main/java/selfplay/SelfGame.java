package selfplay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.models.Player;
import org.example.models.base.GameState;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;


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
