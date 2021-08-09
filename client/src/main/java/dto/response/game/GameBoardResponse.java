package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.GameState;
import models.base.interfaces.GameBoard;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GameBoardResponse extends GameResponse {
    private int gameId;
    private GameState state;
    private GameBoard board;
    private PlayerResponse opponent;

}
