package controllers;

import dto.request.GameRequest;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import org.apache.log4j.Logger;
import services.PlayerService;
import utils.JsonService;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

public class ConnectionController extends Thread {
    private final ClientConnection connection;
    private final LinkedBlockingDeque<TaskRequest> requests;
    private static Logger logger;

    public ConnectionController(final ClientConnection connection, final LinkedBlockingDeque<TaskRequest> requests) {
        this.connection = connection;
        this.requests = requests;
        logger = Logger.getLogger(ConnectionController.class);
    }

    @Override
    public void run() {
        logger.debug("PlayerController started");
        try {
            while (connection.isConnected()) {
                try {
                    final String msg = connection.readMsg();
                    final GameRequest request = JsonService.getRequestFromMsg(msg);
                    requests.putLast(TaskRequest.create(connection, request));
                } catch (final ServerException e) {
                    logger.warn(e.getMessage());
                }
            }
        } catch (final InterruptedException | IOException e) {
            logger.info(String.format("Close connect with %s", connection));
            try {
                connection.close();
                PlayerService.autoLogoutPlayer(connection);
            } catch (final ServerException exception) {
                logger.error(String.format("Cant logout user after leave %s", connection));
            }
        }
    }

}
