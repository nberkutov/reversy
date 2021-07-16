package dto.response;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ClientConnection;
import services.JsonService;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private ClientConnection client;
    private GameResponse response;

    public static TaskResponse create(ClientConnection connection, GameResponse response) {
        return new TaskResponse(connection, response);
    }

    public void sendJson() throws IOException, GameException {
        if (client.isConnected()) {
            client.send(JsonService.toJsonParser(response));
        }
    }

}
