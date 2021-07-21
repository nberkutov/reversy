package controllers;

import dto.request.TaskRequest;
import dto.response.TaskResponse;
import dto.response.player.MessageResponse;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class ServerController {
    private final LinkedBlockingDeque<TaskRequest> requests;

    public ServerController() {
        requests = new LinkedBlockingDeque<>();
        LinkedBlockingDeque<ClientConnection> waiting = new LinkedBlockingDeque<>();

        ExecutorService serviceHandlerTasks = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 4; i++) {
            serviceHandlerTasks.execute(new TasksHandler(requests, waiting));
        }

        GameSearcher gameSearcher = new GameSearcher(requests, waiting);
        gameSearcher.start();
    }

    public void createControllerForPlayer(final Socket socket) {
        try {
            ClientConnection connection = new ClientConnection(socket);
            motdForPlayer(connection);
            PlayerController.initPlayerController(connection, requests);
        } catch (IOException | GameException e) {
            log.error("CreatePlayerController", e);
        }

    }

    private void motdForPlayer(final ClientConnection connection) throws IOException, GameException {
        TaskResponse.createAndSend(connection, new MessageResponse("Welcome to our server"));
        TaskResponse.createAndSend(connection, new MessageResponse("Whats you name?"));
    }
}
