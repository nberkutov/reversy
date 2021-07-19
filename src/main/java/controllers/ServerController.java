package controllers;

import dto.request.TaskRequest;
import dto.response.MessageResponse;
import dto.response.TaskResponse;
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
    private final LinkedBlockingDeque<TaskResponse> responses;
    private final LinkedBlockingDeque<ClientConnection> waiting;

    public ServerController() {
        requests = new LinkedBlockingDeque<>();
        responses = new LinkedBlockingDeque<>();
        waiting = new LinkedBlockingDeque<>();

        ExecutorService serviceHandlerTasks = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 4; i++) {
            serviceHandlerTasks.execute(new HandlerTasks(requests, responses, waiting));
        }

        SenderTasks senderTasks = new SenderTasks(responses);
        GameSearcher gameSearcher = new GameSearcher(requests, waiting);

        senderTasks.start();
        gameSearcher.start();
    }

    public void createControllerForPlayer(final Socket socket) {
        try {
            ClientConnection connection = new ClientConnection(socket);
            motdForPlayer(connection);
            PlayerController.initPlayerController(connection, requests);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void motdForPlayer(final ClientConnection connection) throws InterruptedException {
        responses.putLast(TaskResponse.create(connection, new MessageResponse("Welcome to our server")));
        responses.putLast(TaskResponse.create(connection, new MessageResponse("Whats you name?")));
    }
}
