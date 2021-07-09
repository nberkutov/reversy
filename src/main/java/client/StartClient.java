package client;

import lombok.extern.slf4j.Slf4j;
import services.Server;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class StartClient {
    private static final String IP = "127.0.0.1";//"localhost";
    private static final int PORT = Server.PORT;

    public static void main(String[] args) throws IOException {
        try {
            Client client = new Client(IP, PORT);
            Thread thread = new Thread(client);
            thread.start();
            thread.join();
        } catch (IOException | InterruptedException e) {
            log.error("ERROR", e);
        }

    }

}
