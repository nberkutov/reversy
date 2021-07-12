package models;

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
    protected PlayerColor color;
    protected ClientConnection connection;

    public Player(long id) {
        this.id = id;
    }

    public Player(long id, PlayerColor color) {
        this.id = id;
        this.color = color;
    }

    public void initConnect(Socket socket) throws IOException {
        if (connection != null) {
            connection.close();
        }

        initConnect(new ClientConnection(socket));
    }

    public void initConnect(ClientConnection connection) {
        this.connection = connection;
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
