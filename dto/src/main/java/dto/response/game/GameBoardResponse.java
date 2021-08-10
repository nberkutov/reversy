package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.base.GameState;
import models.base.interfaces.GameBoard;


@Data
@EqualsAndHashCode
@AllArgsConstructor
public class GameBoardResponse implements GameResponse {
    private final int gameId;
    private final GameState state;
    private final GameBoard board;
    private final PlayerResponse opponent;
}
