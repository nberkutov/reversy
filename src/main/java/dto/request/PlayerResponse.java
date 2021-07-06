package dto.request;

import lombok.Data;
import models.Cell;
import models.Point;
import models.base.GameState;

import java.util.Map;
import java.util.Set;

@Data
public class PlayerResponse {
    private GameState state;
    private Map<Point, Cell> board;
    private Set<Point> available;
}
