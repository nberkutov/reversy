package dto.request.player;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Point;

import static models.Board.BOARD_SIZE;

@Data
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
