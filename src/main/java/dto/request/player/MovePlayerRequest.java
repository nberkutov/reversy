package dto.request.player;

import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static models.Board.BOARD_SIZE;

@Data
@NoArgsConstructor
public class MovePlayerRequest extends GameRequest {
    private int idPlayer;
    private int idGame;
    private int x;
    private int y;

    public MovePlayerRequest(int idPlayer, int idGame, int x, int y) throws GameException {
        this(x, y);
        this.idPlayer = idPlayer;
        this.idGame = idGame;
    }

    public MovePlayerRequest(int x, int y) throws GameException {
        this.x = x;
        this.y = y;
        checkXY();
    }

    private void checkXY() throws GameException {
        if (x >= 0 && y >= 0 && x < BOARD_SIZE && y < BOARD_SIZE) {
            return;
        }
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }
}
