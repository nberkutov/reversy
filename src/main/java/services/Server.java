package services;

import controllers.GameController;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Server {
    public static final int PORT = 8081;
    private final GameController controller = new GameController();

    public void Start() throws IOException {
        controller.start();
        try (final ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.debug("Server stated {}", serverSocket);
            while (true) {
                final Socket socket = serverSocket.accept();
                new Thread(() -> {
                    connect(socket);
                }).start();
            }
        } catch (final BindException e) {
            log.error("ERROR", e);
        }
    }

    private void connect(Socket socket) {
        try {
            log.debug("Found connect {}", socket);
            controller.addPlayer(socket);
        } catch (IOException | InterruptedException e) {
            log.error("ERROR", e);
        }
    }
}
