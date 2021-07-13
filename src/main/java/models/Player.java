package models;

import exception.GameErrorCode;
import models.base.PlayerState;
import services.BoardService;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player implements Delayed {
    private long id;
    private PlayerState state;
    protected PlayerColor color;
    protected ClientConnection connection;

    public Player(long id) {
        this.id = id;
        state = PlayerState.NONE;
    }

    public Player(long id, PlayerColor color) {
        this(id);
        this.color = color;
    }

    public void initConnect(ClientConnection connection) {
        this.connection = connection;
    }

    public void closeConnect() {
        if (connection != null) {
            connection.close();
        }
        connection = null;
    }

    public void nextMove(Game game) throws GameException {
        throw new NotImplementedException();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}
