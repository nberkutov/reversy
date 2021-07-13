package models.player;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ClientConnection;
import models.game.Game;
import models.base.PlayerColor;
import models.base.PlayerState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player implements Delayed {
    private int id;
    private PlayerState state;
    protected PlayerColor color;
    protected ClientConnection connection;

    public Player(final int id) {
        this.id = id;
        state = PlayerState.NONE;
    }

    public Player(final int id, final PlayerColor color) {
        this(id);
        this.color = color;
    }

    public void initConnect(final ClientConnection connection) {
        this.connection = connection;
    }

    public void closeConnect() {
        if (connection != null) {
            connection.close();
        }
        connection = null;
    }

    public void nextMove(final Game game) throws GameException {
        throw new NotImplementedException();
    }

    @Override
    public long getDelay(final TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo(final Delayed object) {
        return 0;
    }
}