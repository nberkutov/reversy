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
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import services.utils.StatisticUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

import static models.GameProperties.SERVER_FILE;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class Server extends Thread implements AutoCloseable {
    private static final Scanner scanner = new Scanner(System.in);
    public static DataBase database;

    private final int port;
    private final ServerHandler serverHandler;

    private ServerSocket serverSocket;
    private ServerProperties properties;

    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Не указан путь до файла конфигурации.");
            return;
        }
        final File configFile = new File(args[0]);
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            final ServerProperties properties = mapper.readValue(configFile, ServerProperties.class);
            initLogger(properties);
            try (final Server server = new Server(properties)) {
                server.start();
                server.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static void initLogger(final ServerProperties properties) throws IOException {
        final FileAppender servicesFileAppender = (RollingFileAppender) Logger.getLogger("services")
                .getAllAppenders().nextElement();
        final FileAppender controllersFileAppender = (RollingFileAppender) Logger.getLogger("controllers")
                .getAllAppenders().nextElement();
        final String logDir = properties.getLogPath().orElse("tmp");

        final String servicesLogFileName = logDir + File.separator + "services.log";
        servicesFileAppender.setFile(servicesLogFileName);
        servicesFileAppender.activateOptions();

        final String controllersLogFileName = logDir + File.separator + "controllers.log";
        controllersFileAppender.setFile(controllersLogFileName);
        controllersFileAppender.activateOptions();
    }

    public Server(final int port, final DataBase database) {
        this.port = port;
        Server.database = database;
        this.serverHandler = new ServerHandler();
    }

    public Server(final ServerProperties properties) {
        this.properties = properties;
        this.port = properties.getPort().orElse(8080);
        serverHandler = new ServerHandler();
        database = new DataBase();
    }

    public Server() {
        this(8080, new DataBase());
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            log.debug("Server stated {}", serverSocket);
            while (!serverSocket.isClosed()) {
                final Socket socket = serverSocket.accept();
                serverHandler.createControllerForPlayer(socket);
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

    public void saveStatistic() throws IOException {
        final String dirPath = properties.getStatsPath().orElse("stats");
        final File statsDir = new File(dirPath);
        if (!statsDir.exists() && !statsDir.mkdirs()) {
            throw new IOException(String.format("Не удалось создать директорию %s", dirPath));
        }
        final File statsFile = new File(dirPath + "stats.csv");
        if (!statsFile.exists() && !statsFile.createNewFile()) {
            throw new IOException(String.format("Не удалось создать файл %s", statsFile));
        }
        final List<User> userList = database.getAllPlayers();
        try {
            StatisticUtils.saveStatistic(userList, statsFile.getAbsolutePath());
            log.info("Statistic save in {}", statsFile.getAbsolutePath());
        } catch (final ServerException e) {
            log.error("Cant save statistic", e);
        }
    }

    public void saveServerFile(final String path) {
        if (path != null) {
            try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                final DataBase dataBase = Server.database.clone();
                dataBase.removeAllConnects();
                oos.writeObject(dataBase);
                log.info("Server successfully save database in {}", path);
            } catch (final IOException e) {
                log.error("Server cant save {}", path, e);
            }
        }
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
            }

        } catch (final NumberFormatException | PatternSyntaxException ignore) {
        }
        //log.warn("Game not found");
    }

    public void getInfoRoom() {
        try {
            final Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the id: ");
            final int id = scanner.nextInt();
            final Room room = database.getRoomById(id);
            if (room != null) {
                System.out.println(room);
            }
        } catch (final RuntimeException ignore) {
        }
        //log.warn("Room not found");
    }

    public void getInfoUser() {
        final Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the nickname: ");
        final String nickname = scanner.nextLine().trim().toLowerCase();
        final User user = database.getPlayerByNickname(nickname);
        if (user == null) {
            //log.warn("User not found with {}", nickname);
            return;
        }
        System.out.println(user);
    }
}
