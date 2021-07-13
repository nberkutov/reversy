package controllers;

import dto.request.player.TaskRequest;
import dto.response.GameResponse;
import dto.response.MessageResponse;
import dto.response.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Player;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

@Slf4j
public class ServerController {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<TaskResponse> responses;
    private final BlockingQueue<Player> waiting;

    public ServerController() {
        requests = new DelayQueue<>();
        responses = new DelayQueue<>();
        waiting = new DelayQueue<>();
        HandlerTasks handlerTasks = new HandlerTasks(requests, responses, waiting);
        SenderTasks senderTasks = new SenderTasks(requests, responses);
        GameSearcher gameSearcher = new GameSearcher(responses, waiting);
        handlerTasks.start();
        senderTasks.start();
        gameSearcher.start();
    }

    public void addTaskResponse(ClientConnection connection, GameResponse response) throws InterruptedException {
        responses.put(new TaskResponse(connection, response));
    }

    public void createControllerForPlayer(Socket socket) {
        try {
            ClientConnection connection = new ClientConnection(socket);
            motdForPlayer(connection);
            PlayerController.initPlayerController(connection, requests);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void motdForPlayer(ClientConnection connection) throws InterruptedException {
        addTaskResponse(connection, new MessageResponse("Welcome to our server"));
        addTaskResponse(connection, new MessageResponse("Whats you name?"));
    }

}
