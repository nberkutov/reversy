package client.bots;

import gui.WindowGUI;
import gui.dontneed.EmptyGUI;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.ServerException;
import org.example.models.players.SmartBot;
import org.example.models.strategies.MyStrategy;
import org.example.models.strategies.RandomStrategy;
import org.example.models.strategies.algorithms.HardAlgorithm;


@Slf4j
public class StartClient {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8070;

    public static void main(final String[] args) {
        try {
            final Client botClient = new Client(IP, PORT, new SmartBot("random", new RandomStrategy()), new WindowGUI());
            final Client humanClient = new Client(IP, PORT, new SmartBot("mybot", new MyStrategy(3, new HardAlgorithm())), new EmptyGUI());
            botClient.start();
            humanClient.start();
            botClient.join();
            humanClient.join();
        } catch (final InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
