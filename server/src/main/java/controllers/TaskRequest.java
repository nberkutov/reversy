package controllers;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ClientConnection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private ClientConnection client;
    private GameRequest request;

    public static TaskRequest create(final ClientConnection client, final GameRequest request) {
        return new TaskRequest(client, request);
    }

}
