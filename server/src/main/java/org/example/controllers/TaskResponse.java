package org.example.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.response.GameResponse;
import org.example.exception.ServerException;
import org.example.models.player.UserConnection;
import org.example.utils.JsonService;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private UserConnection client;
    private GameResponse response;

    public static void createAndSend(final UserConnection connection, final GameResponse response) throws ServerException {
        final TaskResponse taskResponse = new TaskResponse(connection, response);
        taskResponse.sendJson();
    }

    public void sendJson() throws ServerException {
        client.send(JsonService.toMsgParser(response));
    }

}
