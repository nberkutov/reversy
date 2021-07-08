package dto.response;

import lombok.Data;
import models.Board;
import models.base.Cell;
import models.Point;
import models.base.GameState;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
public class PlayerResponse implements Serializable {
    private GameState state;
    private Board board;
}
