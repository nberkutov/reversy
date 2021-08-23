package org.example.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.controllers.handlers.ServerHandler;
import org.example.exception.ServerException;
import org.example.models.CacheDataBaseDao;
import org.example.models.DataBaseDao;
import org.example.models.GameProperties;
import org.example.models.game.Game;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.example.services.utils.StatisticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

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
            broadcastMessage("The server stops working");
            controller.close();
            serverSocket.close();
        } catch (final IOException e) {
            log.error("Server close", e);
        }
    }

    public void clearDataBase() {
        dataBaseDao.clearAll();
        cacheDataBaseDao.clearAll();
        log.info("Database clear");
    }

    public void closeAllConnects() {
        broadcastMessage("The server kicked you");
        for (final UserConnection connection : cacheDataBaseDao.getAllConnections()) {
            connection.close();
        }
    }

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

    public void getInfoGame() {
        try {
            final Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the id: ");
            final int id = scanner.nextInt();
            final Game game = dataBaseDao.getGameById(id);
            if (game != null) {
                System.out.println(game);
                return;
            }

        } catch (final NumberFormatException | PatternSyntaxException ignore) {
        }
        log.warn("Game not found");
    }

    public void getInfoRoom() {
        try {
            final Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the id: ");
            final int id = scanner.nextInt();
            final Room room = dataBaseDao.getRoomById(id);
            if (room != null) {
                System.out.println(room);
                return;
            }
        } catch (final RuntimeException ignore) {
        }
        log.warn("Room not found");
    }

    public void getInfoUser() {
        final Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the nickname: ");
        final String nickname = scanner.nextLine().trim().toLowerCase();
        final User user = dataBaseDao.getUserByNickname(nickname);
        if (user == null) {
            log.warn("User not found with {}", nickname);
            return;
        }
        System.out.println(user);
    }
}
