package dto.response;

import exception.ServerException;
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

    public static TaskResponse create(final ClientConnection connection, final GameResponse response) {
        return new TaskResponse(connection, response);
    }

    public static void createAndSend(final ClientConnection connection, final GameResponse response) throws IOException, ServerException {
        TaskResponse taskResponse = new TaskResponse(connection, response);
        taskResponse.sendJson();
    }

    public void sendJson() throws IOException, ServerException {
        if (client.isConnected()) {
            client.send(JsonService.toMsgParser(response));
        }
    }

}
