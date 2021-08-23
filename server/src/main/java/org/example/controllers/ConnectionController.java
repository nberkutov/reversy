package org.example.controllers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.GameRequest;
import org.example.exception.ServerException;
import org.example.models.player.UserConnection;
import org.example.services.PlayerService;
import org.example.utils.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;


@Slf4j
@Component
@NoArgsConstructor
@Scope("prototype")
public class ConnectionController extends Thread implements AutoCloseable {
    @Setter
    @Getter
    private UserConnection connection;

    public ConnectionController(final UserConnection connection) {
        this.connection = connection;
    }

    @Autowired
    private LinkedBlockingDeque<TaskRequest> requests;
    @Autowired
    private PlayerService ps;
    @Autowired
    private PlayerController pc;

    public ConnectionController(final UserConnection connection, final LinkedBlockingDeque<TaskRequest> requests) {
        this.connection = connection;
        this.requests = requests;
    }

    public static void initPlayerController(final UserConnection connection, final LinkedBlockingDeque<TaskRequest> requests) {
        final ConnectionController controller = new ConnectionController(connection, requests);
        controller.start();
    }

    @Override
    public void run() {
        log.debug("PlayerController started");
        try {
            receivingRequests();
        } catch (final InterruptedException | IOException e) {
            log.info("Close connect with {}", connection);
        } finally {
            close();
        }
    }

    private void receivingRequests() throws InterruptedException, IOException {
        while (connection.isConnected()) {
            try {
                final String msg = connection.readMsg();
                final GameRequest request = JsonService.getRequestFromMsg(msg);
                requests.putLast(TaskRequest.create(connection, request));
            } catch (final ServerException e) {
                log.warn("Connection controller {}", connection, e);
            }
        }
    }

    @Override
    public void close() {
        connection.close();
        pc.actionAutoLogoutPlayer(connection);
    }
}
