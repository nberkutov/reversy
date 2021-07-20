package dto.request.player;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.board.Point;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MovePlayerRequest extends GameRequest {
    private int gameId;
    private Point point;

    public static MovePlayerRequest toDto(int gameId, Point point) {
        return new MovePlayerRequest(gameId, point);
    }
}
