package dto.request;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import models.Point;

import static models.Board.BOARD_SIZE;

@Data
public class PlayerRequest {
    private int x;
    private int y;

    public PlayerRequest(int x, int y) throws GameException {
        if (x < 0 || y < 0 || x > BOARD_SIZE || y > BOARD_SIZE) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
        this.x = x;
        this.y = y;
    }
}
