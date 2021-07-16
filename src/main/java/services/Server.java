package services;

import controllers.ServerController;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Data
public class Server {
    private final int PORT;
    private final ServerController controller = new ServerController();
    private final BaseService baseService = new BaseService();

    public Server(int PORT) {
        this.PORT = PORT;
    }

    public void Start() throws IOException {
        try (final ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.debug("Server stated {}", serverSocket);
            while (true) {
                final Socket socket = serverSocket.accept();
                connect(socket);
            }
        } catch (final BindException e) {
            log.error("ERROR", e);
        }
    }

    private void connect(Socket socket) {
        log.debug("Found connect {}", socket);
        controller.createControllerForPlayer(socket);
    }

}
