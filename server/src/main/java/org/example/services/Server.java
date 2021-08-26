package org.example.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.controllers.handlers.ServerHandler;
import org.example.exception.ServerException;
import org.example.models.CacheDataBaseDao;
import org.example.models.DataBaseDao;
import org.example.models.GameProperties;
import org.example.models.player.User;
import org.example.services.utils.StatisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Getter
@Component
public class Server extends Thread implements AutoCloseable {
    private final int port;
    private ServerSocket serverSocket;
    @Autowired
    private DataBaseDao dataBaseDao;
    @Autowired
    private CacheDataBaseDao cacheDataBaseDao;

    @Autowired
    private SenderService ss;
    @Autowired
    private ServerHandler controller;

    public Server() {
        this(GameProperties.PORT);
    }

    public Server(final int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            log.debug("Server stated {}", serverSocket);
            while (!serverSocket.isClosed()) {
                final Socket socket = serverSocket.accept();
                connect(socket);
            }
        } catch (final IOException e) {
            log.info("ServerSocket closed");
        }
    }

    public void close() {
        try {
            saveStatistic(GameProperties.STATS_FILE);
            broadcastMessage("The server stops working");
            serverSocket.close();
        } catch (final IOException e) {
            log.error("Server close", e);
        }
    }

    @Transactional(readOnly = true, rollbackFor = ServerException.class)
    public void saveStatistic(final String path) {
        final List<User> userList = dataBaseDao.getAllPlayers();
        try {
            StatisticUtils.saveStatistic(userList, path);
            log.info("Statistic save in {}", path);
        } catch (final ServerException e) {
            log.error("Cant save statistic", e);
        }
    }

    private void connect(final Socket socket) {
        log.debug("Found connect {}", socket);
        controller.createControllerForPlayer(socket);
    }

    private void broadcastMessage(final String message) {
        try {
            ss.broadcastMessageForAll(message);
        } catch (final ServerException e) {
            log.warn("Broadcast message don't send {}", message);
        }
    }
}
