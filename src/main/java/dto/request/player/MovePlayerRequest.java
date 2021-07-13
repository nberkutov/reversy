package dto.request.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import models.Point;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovePlayerRequest extends GameRequest {
    private int playerId;
    private int gameId;
    private Point point;
}
