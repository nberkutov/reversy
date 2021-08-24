package org.example.controllers.handlers;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.ConnectionController;
import org.example.controllers.TaskRequest;
import org.example.controllers.mapper.Mapper;
import org.example.exception.ServerException;
import org.example.models.GameProperties;
import org.example.models.player.UserConnection;
import org.example.services.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
public class ServerHandler implements ApplicationRunner {
    @Autowired
    private LinkedBlockingDeque<TaskRequest> requests;

    @Autowired
    @Qualifier(value = "handlers")
    private TaskExecutor executorHandlers;
    @Autowired
    @Qualifier(value = "clientControllers")
    private TaskExecutor executorClients;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = "requests")
    public LinkedBlockingDeque<TaskRequest> createDequeRequests() {
        return new LinkedBlockingDeque<>();
    }

    @Bean(name = "waitings")
    public LinkedBlockingDeque<UserConnection> createDequeWaiting() {
        return new LinkedBlockingDeque<>();
    }

    @Bean(name = "logouts")
    public LinkedBlockingDeque<UserConnection> createDequeLogout() {
        return new LinkedBlockingDeque<>();
    }


    @Autowired
    private SenderService ss;

    public void createControllerForPlayer(final Socket socket) {
        try {
            final UserConnection connection = new UserConnection(socket);
            motdForPlayer(connection);
            final ConnectionController client = applicationContext.getBean(ConnectionController.class);
            client.setConnection(connection);
            executorClients.execute(client);
        } catch (final IOException | ServerException e) {
            log.error("CreatePlayerController", e);
        }

    }

    private void motdForPlayer(final UserConnection connection) throws ServerException {
        ss.sendResponse(connection, Mapper.toDtoMessage("Welcome to our server"));
        ss.sendResponse(connection, Mapper.toDtoMessage("You can authorize or register"));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < GameProperties.HANDLER_THREADS; i++) {
            final Thread handler = applicationContext.getBean(TasksHandler.class);
            executorHandlers.execute(handler);
        }
        for (int i = 0; i < GameProperties.GAME_SEARCH_THREADS; i++) {
            final Thread searcher = applicationContext.getBean(GameSearcher.class);
            executorHandlers.execute(searcher);
        }

        for (int i = 0; i < GameProperties.CLIENT_AUTO_LOGOUT_THREADS; i++) {
            final Thread logoutHandler = applicationContext.getBean(AutoLogoutHandler.class);
            executorHandlers.execute(logoutHandler);
        }
    }
}
