package controllers;

import dto.response.GameResponse;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ClientConnection;
import utils.JsonService;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private ClientConnection client;
    private GameResponse response;

    public static void createAndSend(final ClientConnection connection, final GameResponse response) throws ServerException {
        final TaskResponse taskResponse = new TaskResponse(connection, response);
        taskResponse.sendJson();
    }

    public void sendJson() throws ServerException {
        client.send(JsonService.toMsgParser(response));
    }

}
