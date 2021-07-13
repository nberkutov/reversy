package dto.request.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.board.Point;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MovePlayerRequest extends GameRequest {
    private int playerId;
    private int gameId;
    private Point point;

    public static MovePlayerRequest toDto(int playerId, int gameId, Point point) {
        return new MovePlayerRequest(playerId, gameId, point);
    }
}
