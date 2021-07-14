package models.player;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.game.Game;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private int id;
    private PlayerState state;
    protected PlayerColor color;

    public Player(final int id) {
        this.id = id;
        state = PlayerState.NONE;
    }

    public Player(final int id, final PlayerColor color) {
        this(id);
        this.color = color;
    }

    public void nextMove(final Game game) throws GameException {
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
