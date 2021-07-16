import services.Server;

import java.io.IOException;

public class StartServer {
    public static final int PORT = 8081;
    public static void main(String[] args) throws IOException {
        Server server = new Server(PORT);
        server.Start();
    }
}
