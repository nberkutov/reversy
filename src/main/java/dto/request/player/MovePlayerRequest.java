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
    private int gameId;
    private Point point;

    public static MovePlayerRequest toDto(int gameId, Point point) {
        return new MovePlayerRequest(gameId, point);
    }
}
