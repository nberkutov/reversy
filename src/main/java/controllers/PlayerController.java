package controllers;

import dto.request.player.TaskRequest;
import dto.response.GameResponse;
import dto.response.TaskResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

@Data
@AllArgsConstructor
@Slf4j
public class PlayerController extends Thread {
    private final ClientConnection connection;
    private final BlockingQueue<TaskRequest> requests;

    @Override
    public void run() {
        log.debug("PlayerController started");

        while (connection.isConnected()) {
            try {
                TaskRequest request = TaskRequest.getTaskRequest(connection);
                requests.add(request);
            } catch (GameException | IOException e) {
                if (connection != null) {
                    connection.close();
                    this.interrupt();
                }
            }
        }
    }

    public static void initPlayerController(ClientConnection connection, BlockingQueue<TaskRequest> requests){
        PlayerController controller = new PlayerController(connection, requests);
        controller.start();
    }

}
