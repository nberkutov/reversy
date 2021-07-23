package services;

import controllers.handlers.ServerHandler;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Data
public class Server implements Runnable {
    private final int PORT;
    private final ServerHandler controller;
    private final DataBaseService dataBaseService;

    public Server(final int PORT, final DataBaseService dataBaseService) {
        this.PORT = PORT;
        this.dataBaseService = dataBaseService;
        this.controller = new ServerHandler();
    }

    public Server(final int PORT) {
        this(PORT, new DataBaseService());
    }

    @SneakyThrows
    @Override
    public void run() {
        Start();
    }

    private void Start() {
        try (final ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.debug("Server stated {}", serverSocket);
            while (true) {
                try {
                    final Socket socket = serverSocket.accept();
                    connect(socket);
                } catch (IOException e) {
                    log.error("Error connection with socket", e);
                }
            }
        } catch (IOException e) {
            log.error("Error server", e);
        }
    }

    private void connect(Socket socket) {
        log.debug("Found connect {}", socket);
        controller.createControllerForPlayer(socket);
    }


}
