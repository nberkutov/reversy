package dto.request.player;

import controllers.commands.CommandRequest;
import exception.GameErrorCode;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ClientConnection;

import java.io.IOException;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest implements Delayed {
    private ClientConnection client;
    private GameRequest request;

    public TaskRequest(final ClientConnection client) {
        this.client = client;
    }

    public static TaskRequest getTaskRequest(ClientConnection client) throws GameException, IOException {
        if (!client.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }
        String msg = client.getIn().readUTF();
        GameRequest request = CommandRequest.getRequestFromJson(msg);
        return new TaskRequest(client, request);
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
