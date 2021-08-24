package client;

import gui.Ui;
import org.example.exception.ServerException;
import org.example.models.GameProperties;

public class Main {
    private static final String IP = "127.0.0.1";

    private static final int PORT = GameProperties.PORT;

    public static void main(final String[] args) {
        try {
            final GUIClient client = new GUIClient(IP, PORT, new Ui());
            client.start();
            client.join();
        } catch (final InterruptedException | ServerException e) {
            System.exit(0);
        }
    }
}
