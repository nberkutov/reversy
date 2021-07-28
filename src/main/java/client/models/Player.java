package client.models;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Player {
    protected PlayerColor color;
    private String nickname;

    public Player(String nickname) {
        this.nickname = nickname;
    }

    public abstract Point move(final GameBoard board) throws GameException;
}
