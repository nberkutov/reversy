package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.game.Game;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GameBoardResponse extends GameResponse {
    private int gameId;
    private GameState state;
    private GameBoard board;

    public static GameBoardResponse toDto(final Game game) {
        return new GameBoardResponse(game.getId(), game.getState(), game.getBoard());
    }
}
