package org.example.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.request.GameRequest;
import org.example.models.player.UserConnection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private UserConnection client;
    private GameRequest request;

    public static TaskRequest create(final UserConnection client, final GameRequest request) {
        return new TaskRequest(client, request);
    }

}
