package dto.response;

import controllers.commands.CommandResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Board;
import models.Game;
import models.base.Cell;
import models.Point;
import models.base.GameState;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static services.BaseService.GSON;

@NoArgsConstructor
@AllArgsConstructor
public class GameBoardResponse extends GameResponse {
    private GameState state;
    private Board board;

    public static GameBoardResponse toDto(Game game) {
        return new GameBoardResponse(game.getState(), game.getBoard());
    }
}
