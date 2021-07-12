package dto.request.player;

import com.google.gson.Gson;
import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ClientConnection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest implements Delayed {
    private Gson gson = new Gson();
    private ClientConnection client;

    public TaskRequest(ClientConnection client) {
        this.client = client;
    }

    public <T> T getRequest(Class<T> obj) throws GameException, IOException {

        if (!client.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }
        String msg = client.getIn().readLine();
        return gson.fromJson(msg, obj);
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
