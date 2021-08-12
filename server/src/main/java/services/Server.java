package services;

import base.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import controllers.handlers.ServerHandler;
import controllers.handlers.TasksHandler;
import controllers.mapper.Mapper;
import exception.ServerException;
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

import static models.GameProperties.SERVER_FILE;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class Server extends Thread implements AutoCloseable {
    private static final Scanner scanner = new Scanner(System.in);
    private final int socketNumber;
    private final int port;
    private final ServerHandler serverHandler;
    public static DataBase database;
    private ServerSocket serverSocket;

    public static void main(final String[] args) {
        if (args.length == 0) {
            log.info("Config file missed.");
            return;
        }
        final File configFile = new File(args[0]);
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            final ServerProperties properties = mapper.readValue(configFile, ServerProperties.class);
            final int socketsNumber = properties.getThreads().orElse(2);
            System.out.println(properties);
            try (final Server server = new Server(socketsNumber, properties.getPort().orElse(8080), new DataBase())) {
                server.start();
                handleCommands(server);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleCommands(final Server server) {
        while (true) {
            final String input = scanner.nextLine().trim().toLowerCase();
            final Command command = Command.parse(input, server, SERVER_FILE);
            command.execute();
        }
    }

    public Server(final int socketsNumber, final int port, final DataBase database) {
        this.socketNumber = socketsNumber;
        this.port = port;
        Server.database = database;
        this.serverHandler = new ServerHandler();
    }

    public Server() {
        this(2, 8000, new DataBase());
    }

    /*public static Server initServerFromFile(final int port, final String path) {
        final DataBase dataBase = new DataBase();
        //final Server server = new Server(socketsNumber, port, dataBase);
        if (path != null) {
            uploadServerFile(path);
        }
        return server;
    }*/

    /*public static void uploadServerFile(final String path) {
        if (path == null) {
            log.error("Upload server stop, path is invalid");
            return;
        }
        try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)))) {
            database = (DataBase) ois.readObject();
            log.info("Server found and upload database in {}", path);
            log.info("Database found: {}", database);
        } catch (final IOException | ClassNotFoundException e) {
            log.error("Upload server from file not successfully {}", e.getMessage());
        }
    }*/

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
            serverHandler.close();
            serverSocket.close();
            Thread.currentThread().interrupt();
        } catch (final IOException e) {
            log.error("Server close", e);
        }
    }

    public void clearDataBase() {
        database.clearAll();
        log.info("Database clear");
    }

    public void closeAllConnects() {
        broadcastMessage("The server kicked you");
        for (final ClientConnection connection : database.getAllConnection()) {
            connection.close();
        }
    }

    public void saveStatistic(final String path) {
        final List<User> userList = database.getAllPlayers();
        try {
            StatisticUtils.saveStatistic(userList, path);
            log.info("Statistic save in {}", path);
        } catch (final ServerException e) {
            log.error("Cant save statistic", e);
        }
    }

    public void saveServerFile(final String path) {
        if (path != null) {
            try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)))) {
                final DataBase dataBase = Server.database.clone();
                dataBase.removeAllConnects();
                oos.writeObject(dataBase);
                log.info("Server successfully save database in {}", path);
            } catch (final IOException e) {
                log.error("Server cant save {}", path, e);
            }
        }
    }

    private void connect(final Socket socket) {
        log.debug("Found connect {}", socket);
        serverHandler.createControllerForPlayer(socket);
    }

    private void broadcastMessage(final String message) {
        try {
            final List<ClientConnection> list = database.getAllConnection();
            TasksHandler.broadcastResponse(list, Mapper.toDto(message));
        } catch (final IOException | ServerException e) {
            log.warn("Broadcast message don't send {}", message);
        }
    }

    public void getGameInfo() {
        try {
            final Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the id: ");
            final int id = scanner.nextInt();
            final Game game = database.getGameById(id);
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
            final Room room = database.getRoomById(id);
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
        final User user = database.getPlayerByNickname(nickname);
        if (user == null) {
            log.warn("User not found with {}", nickname);
            return;
        }
        System.out.println(user);
    }
}
