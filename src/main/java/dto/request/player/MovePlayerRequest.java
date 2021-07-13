package dto.request.player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import models.board.Point;

@Getter
@NoArgsConstructor
public class MovePlayerRequest extends GameRequest {
    private int playerId;
    private int gameId;
    private Point point;

    public MovePlayerRequest(int playerId, int gameId, Point point) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.point = point;
    }
}
