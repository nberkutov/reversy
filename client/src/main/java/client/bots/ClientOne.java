package client.bots;

import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.ServerException;
import org.example.models.GameProperties;
import org.example.models.players.SmartBot;
import org.example.models.strategies.MyStrategy;
import org.example.models.strategies.algorithms.HardAlgorithm;

@Slf4j
public class ClientOne {
    private static final String IP = "127.0.0.1";

    private static final int PORT = GameProperties.PORT;

    public static void main(final String[] args) {
        try {
            final Client botClient = new Client(IP, PORT, new SmartBot("myBot", new MyStrategy(3, new HardAlgorithm())), new WindowGUI());
            botClient.start();
            botClient.join();
        } catch (final InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
