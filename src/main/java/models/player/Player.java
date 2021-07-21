package models.player;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.interfaces.GameBoard;
import models.base.interfaces.GamePlayer;
import models.board.Point;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player extends User implements Serializable, GamePlayer {
    private PlayerState state;
    protected PlayerColor color;

    public Player(final int id, final String nickname) {
        super(id, nickname);
        state = PlayerState.NONE;
        color = PlayerColor.NONE;
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        return null;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", nickname='" + nickname +
                "', state=" + state +
                ", color=" + color + +'\'' +
                '}';
    }
}
