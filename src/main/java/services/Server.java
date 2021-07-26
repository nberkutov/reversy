package services;

import controllers.handlers.ServerHandler;
import controllers.handlers.TasksHandler;
import dto.response.player.MessageResponse;
import exception.GameException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.DataBase;
import models.game.Game;
import models.game.Room;
import models.player.User;
import services.utils.StatisticUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class Server extends Thread implements AutoCloseable {
    private final int PORT;
    private final ServerHandler controller;
    public static DataBase dataBase;
    private ServerSocket serverSocket;

    public Server(final int PORT, final DataBase dataBase) {
        this.PORT = PORT;
        Server.dataBase = dataBase;
        this.controller = new ServerHandler();
    }

    public Server() {
        this(8000, new DataBase());
    }

    public static Server initServerFromFile(final int port, final String path) {
        DataBase dataBase = new DataBase();
        Server server = new Server(port, dataBase);
        if (path != null) {
            uploadServerFile(path);
        }
        return server;
    }

    public static void uploadServerFile(String path) {
        if (path == null) {
            log.error("Upload server stop, path invalid {}", path);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)))) {
            dataBase = (DataBase) ois.readObject();
            log.info("Server found and upload database in {}", path);
            log.info("Database found: {}", dataBase);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Upload server from file not successfully {} {}", path, e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            log.debug("Server stated {}", serverSocket);
            while (!serverSocket.isClosed()) {
                final Socket socket = serverSocket.accept();
                connect(socket);
            }
        } catch (IOException e) {
            log.info("ServerSocket closed");
        }
    }

    public void close() {
        try {
            broadcastMessage("The server stops working");
            controller.close();
            serverSocket.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("Server close", e);
        }
    }

    public void clearDataBase() {
        dataBase.clearAll();
        log.info("Database clear");
    }

    public void closeAllConnects() {
        broadcastMessage("The server kicked you");
        for (ClientConnection connection : dataBase.getAllConnection()) {
            try {
                connection.close();
                PlayerService.autoLogoutPlayer(connection);
            } catch (GameException exception) {
                log.error("Cant logout player {}", connection);
            }
        }
    }

    public void saveStatistic(final String path) {
        List<User> userList = dataBase.getAllPlayers();
        try {
            StatisticUtils.saveStatistic(userList, path);
            log.info("Statistic save in {}", path);
        } catch (GameException e) {
            log.error("Cant save statistic", e);
        }
    }

    public void saveServerFile(final String path) {
        if (path != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)))) {
                DataBase dataBase = Server.dataBase.clone();
                dataBase.removeAllConnects();
                oos.writeObject(dataBase);
                log.info("Server successfully save database in {}", path);
            } catch (IOException e) {
                log.error("Server cant save {}", path, e);
            }
        }
    }

    private void connect(final Socket socket) {
        log.debug("Found connect {}", socket);
        controller.createControllerForPlayer(socket);
    }

    private void broadcastMessage(final String message) {
        try {
            List<ClientConnection> list = dataBase.getAllConnection();
            MessageResponse messageResponse = new MessageResponse(message);
            TasksHandler.broadcastResponse(list, messageResponse);
        } catch (IOException | GameException e) {
            log.warn("Broadcast message don't send {}", message);
        }
    }

    public void getInfoGame() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the id: ");
            int id = scanner.nextInt();
            Game game = dataBase.getGameById(id);
            if (game != null) {
                System.out.println(game);
                return;
            }
        } catch (NumberFormatException | PatternSyntaxException ignore) {
        }
        log.warn("Game not found");
    }

    public void getInfoRoom() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the id: ");
            int id = scanner.nextInt();
            Room room = dataBase.getRoomById(id);
            if (room != null) {
                System.out.println(room);
                return;
            }
        } catch (RuntimeException ignore) {
        }
        log.warn("Room not found");
    }

    public void getInfoUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the nickname: ");
        String nickname = scanner.nextLine().trim().toLowerCase();
        User user = dataBase.getPlayerByNickname(nickname);
        if (user == null) {
            log.warn("User not found with {}", nickname);
            return;
        }
        System.out.println(user);
    }
}
