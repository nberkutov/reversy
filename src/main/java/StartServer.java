import services.Server;

public class StartServer {
    public static final int PORT = 8081;
    public static void main(String[] args) {
        Server server = new Server(PORT);
        Thread thread = new Thread(server);
        thread.start();
    }
}
