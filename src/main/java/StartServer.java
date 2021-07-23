import models.GameProperties;
import services.Server;

public class StartServer {
    public static void main(String[] args) {
        Server server = new Server(GameProperties.PORT);
        Thread thread = new Thread(server);
        thread.start();
    }
}
