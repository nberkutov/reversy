package models.player;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.interfaces.GameBoard;
import models.base.interfaces.GamePlayer;
import models.board.Point;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player implements Serializable, GamePlayer {
    private int id;
    private PlayerState state;
    protected PlayerColor color;

    public Player(final int id) {
        this.id = id;
        state = PlayerState.NONE;
    }

    public Player(final PlayerColor color) {
        this.color = color;
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        return null;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", state=" + state +
                ", color=" + color +
                '}';
    }


}
