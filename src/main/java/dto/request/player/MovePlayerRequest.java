package dto.request.player;

import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Point;

import static models.Board.BOARD_SIZE;

@Data
@NoArgsConstructor
public class MovePlayerRequest extends GameRequest {
    private int idPlayer;
    private int idGame;
    private Point point;

    public MovePlayerRequest(int idPlayer, int idGame, Point point) throws GameException {
        this(point);
        this.idPlayer = idPlayer;
        this.idGame = idGame;
    }

    public MovePlayerRequest(Point point) throws GameException {
        checkXY(point);
        this.point = point;
    }

    private void checkXY(Point point) throws GameException {
        if (point.getX() >= 0 && point.getY() >= 0 && point.getX() < BOARD_SIZE && point.getY() < BOARD_SIZE) {
            return;
        }
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }
}
