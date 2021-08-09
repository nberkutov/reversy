package controllers;

import dto.request.GameRequest;
import dto.request.TaskRequest;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.JsonService;
import services.PlayerService;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;


@AllArgsConstructor
@Slf4j
public class ConnectionController extends Thread {
    private final ClientConnection connection;
    private final LinkedBlockingDeque<TaskRequest> requests;

    public static void initPlayerController(final ClientConnection connection, final LinkedBlockingDeque<TaskRequest> requests) {
        ConnectionController controller = new ConnectionController(connection, requests);
        controller.start();
    }

    @Override
    public void run() {
        log.debug("PlayerController started");
        try {
            while (connection.isConnected()) {
                try {
                    String msg = connection.readMsg();
                    GameRequest request = JsonService.getRequestFromMsg(msg);
                    requests.putLast(TaskRequest.create(connection, request));
                } catch (ServerException e) {
                    log.warn("Connection controller {}", connection, e);
                }
            }
        } catch (InterruptedException | IOException e) {
            log.info("Close connect with {}", connection);
            try {
                connection.close();
                PlayerService.autoLogoutPlayer(connection);
            } catch (ServerException exception) {
                log.error("Cant logout user after leave {}", connection);
            }

        }
    }

}
