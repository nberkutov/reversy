package dto.response;

import controllers.commands.CommandResponse;
import exception.GameException;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ClientConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor

public class TaskResponse implements Delayed {
    private ClientConnection client;
    private List<GameResponse> responses;


    public TaskResponse(final ClientConnection client, final GameResponse response) {
        responses = new ArrayList<>();
        this.client = client;
        responses.add(response);
    }

    public TaskResponse(ClientConnection client, List<GameResponse> responses) {
        this.client = client;
        this.responses = responses;
    }

    public void sendJson() throws IOException, GameException {
        if (client.isConnected()) {
            for (GameResponse response : responses) {
                client.send(CommandResponse.toJsonParser(response));
            }
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
