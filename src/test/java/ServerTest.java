import org.junit.jupiter.api.Test;
import services.Server;

import java.io.IOException;

public class ServerTest {
    @Test
    void startServer() throws IOException {
        Server server = new Server();
        server.Start();
    }
}
