import client.Client;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import services.Server;

import java.io.IOException;

public class ClientTest {
    private static final String IP = "127.0.0.1";//"localhost";
    private static final int PORT = Server.PORT;
    @Test
    void testClient() throws IOException, InterruptedException {
        Client client = new Client(IP, PORT);
        Thread thread = new Thread(client);
        thread.start();
        thread.join();
    }

    //@Test
    void startServer() throws IOException, InterruptedException {
        new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Server server = new Server();
                server.Start();
            }
        };
        Thread.sleep(1000);
        Client client = new Client(IP, PORT);
        Thread thread = new Thread(client);
        thread.start();
        thread.join();
    }
}
