package controllers.handlers;

import controllers.ConnectionController;
import controllers.TaskRequest;
import controllers.mapper.Mapper;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.SenderService;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class ServerHandler implements AutoCloseable {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final LinkedBlockingDeque<ClientConnection> waiting;
    private final ExecutorService serviceHandlerTasks;
    private final GameSearcher gameSearcher;

    public ServerHandler() {
        requests = new LinkedBlockingDeque<>();
        waiting = new LinkedBlockingDeque<>();
        serviceHandlerTasks = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 4; i++) {
            final TasksHandler handler = new TasksHandler(requests, waiting);
            serviceHandlerTasks.execute(handler);
        }

        gameSearcher = new GameSearcher(waiting);
        gameSearcher.start();
    }

    public void createControllerForPlayer(final Socket socket) {
        try {
            final ClientConnection connection = new ClientConnection(socket);
            motdForPlayer(connection);
            ConnectionController.initPlayerController(connection, requests);
        } catch (final IOException | ServerException e) {
            log.error("CreatePlayerController", e);
        }

    }

    private void motdForPlayer(final ClientConnection connection) throws ServerException {
        SenderService.sendResponse(connection, Mapper.toDtoMessage("Welcome to our server"));
        SenderService.sendResponse(connection, Mapper.toDtoMessage("You can authorize or register"));
    }

    public void close() {
        serviceHandlerTasks.shutdownNow();
        gameSearcher.interrupt();
    }
}
