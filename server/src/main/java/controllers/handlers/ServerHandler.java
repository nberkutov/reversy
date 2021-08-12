package controllers.handlers;

import controllers.ConnectionController;
import controllers.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class ServerHandler implements AutoCloseable {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final ExecutorService tasksHandlerSevice;
    private final GameSearcher gameSearcher;

    public ServerHandler() {
        requests = new LinkedBlockingDeque<>();
        final LinkedBlockingDeque<ClientConnection> waiting = new LinkedBlockingDeque<>();
        tasksHandlerSevice = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 4; i++) {
            tasksHandlerSevice.execute(new TasksHandler(requests, waiting));
        }

        gameSearcher = new GameSearcher(requests, waiting);
        gameSearcher.start();
    }

    public void createControllerForPlayer(final Socket socket) {
        try {
            final ClientConnection connection = new ClientConnection(socket);
            ConnectionController.initPlayerController(connection, requests);
        } catch (final IOException e) {
            log.error("CreatePlayerController", e);
        }

    }

    public void close() {
        tasksHandlerSevice.shutdownNow();
        gameSearcher.interrupt();
    }
}
