package dto.response;

import com.google.gson.Gson;
import controllers.commands.CommandResponse;
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

public class TaskResponse implements Delayed {
    private ClientConnection client;
    private GameResponse response;

    public TaskResponse(ClientConnection client, GameResponse response) {
        this.client = client;
        this.response = response;
    }

    public void sendJson() throws IOException, GameException {
        if (client.isConnected()) {
            client.send(CommandResponse.toJsonParser(response));
        }
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
