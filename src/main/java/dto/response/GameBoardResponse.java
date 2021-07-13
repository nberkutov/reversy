package dto.response;

import controllers.commands.CommandResponse;
import lombok.Data;
import models.Board;
import models.base.Cell;
import models.Point;
import models.base.GameState;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static services.BaseService.GSON;

@Data
public class GameBoardResponse extends GameResponse {
    private GameState state;
    private Board board;
}
