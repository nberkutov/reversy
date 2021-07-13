package controllers;

import com.google.gson.Gson;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.TaskRequest;
import dto.response.GameResponse;
import dto.response.MessageResponse;
import dto.response.TaskResponse;
import dto.response.player.CreatePlayerResponse;
import exception.GameException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Player;
import services.BaseService;
import services.GameService;
import services.PlayerService;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

@Slf4j
public class ServerController {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<TaskResponse> responses;

    private final BlockingQueue<Player> waiting = new DelayQueue<>();

    public ServerController() {
        requests = new DelayQueue<>();
        responses = new DelayQueue<>();
        HandlerTasks handlerTasks = new HandlerTasks(requests, responses);
        SenderTasks senderTasks = new SenderTasks(requests, responses);
        handlerTasks.start();
        senderTasks.start();
    }

    public void addTaskResponse(Player player, GameResponse response) throws InterruptedException {
        addTaskResponse(player.getConnection(), response);
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
