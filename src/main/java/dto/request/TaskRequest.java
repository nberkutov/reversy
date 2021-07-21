package dto.request;

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

    public static TaskRequest create(ClientConnection client, GameRequest request) {
        return new TaskRequest(client, request);
    }

}
