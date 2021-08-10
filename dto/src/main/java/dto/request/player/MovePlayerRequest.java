package dto.request.player;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.board.Point;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class MovePlayerRequest implements GameRequest {
    private final int gameId;
    private final Point point;

    public static MovePlayerRequest toDto(int gameId, Point point) {
        return new MovePlayerRequest(gameId, point);
    }
}
