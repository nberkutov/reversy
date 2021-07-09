package controllers;

import com.google.gson.Gson;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.TaskRequest;
import dto.response.GameResponse;
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
public class GameController extends Thread {
    private final Gson gson = new Gson();
    private final BlockingQueue<TaskRequest> requests = new DelayQueue<TaskRequest>();
    private final BlockingQueue<TaskResponse> responses = new DelayQueue<TaskResponse>();

    private final BaseService baseService = new BaseService();
    private final BlockingQueue<Player> waiting = new DelayQueue<Player>();


    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            TaskResponse task = responses.take();
            log.debug("Server send response {}", task.getResponse());
            task.sendJson();
        }
    }

    public void addTaskResponse(Player player, GameResponse response) throws InterruptedException {
        addTaskResponse(player.getConnection(), response);
    }

    public void addTaskResponse(ClientConnection connection, GameResponse response) throws InterruptedException {
        responses.put(new TaskResponse(connection, response));
    }

    public void addPlayer(Socket socket) throws IOException, InterruptedException {
        ClientConnection connection = null;
        try {
            connection = new ClientConnection(socket);
            log.debug(connection.getIn().readLine());
//            log.debug("Server get connection {}", connection);
//            addTaskResponse(connection, new GameResponse("Whats you name?"));
//            TaskRequest request = new TaskRequest(connection);
//
//            Player player = PlayerService.createPlayer(request.getRequest(CreatePlayerRequest.class));
//            log.debug("Server get player {}", player);
//            player.initConnect(connection);
//            addTaskResponse(player, CreatePlayerResponse.toDto(player));
//            addTaskResponse(player, new GameResponse("Waiting game"));
//            waiting.put(player);
        } catch (IOException e) {
            if (connection != null) {
                addTaskResponse(connection, new GameResponse("Waiting game"));
                connection.close();
            }
        }
    }
}
