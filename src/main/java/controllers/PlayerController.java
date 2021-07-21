package controllers;

import dto.request.GameRequest;
import dto.request.TaskRequest;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.JsonService;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;


@AllArgsConstructor
@Slf4j
public class PlayerController extends Thread {
    private final ClientConnection connection;
    private final LinkedBlockingDeque<TaskRequest> requests;

    public static void initPlayerController(
            final ClientConnection connection,
            final LinkedBlockingDeque<TaskRequest> requests) {
        PlayerController controller = new PlayerController(connection, requests);
        controller.start();
    }

    @Override
    public void run() {
        log.debug("PlayerController started");
        while (connection.isConnected()) {
            try {
                String msg = connection.readMsg();
                GameRequest request = JsonService.getRequestFromMsg(msg);
                requests.putLast(TaskRequest.create(connection, request));
            } catch (GameException | IOException | InterruptedException e) {
                connection.close();
                this.interrupt();
            }
        }
    }

}
