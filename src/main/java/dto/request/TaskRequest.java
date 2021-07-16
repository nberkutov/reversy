package dto.request;

import dto.request.player.GameRequest;
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

    public TaskRequest(final ClientConnection client) {
        this.client = client;
    }

    public static TaskRequest create(ClientConnection client, GameRequest request) {
        return new TaskRequest(client, request);
    }

}
