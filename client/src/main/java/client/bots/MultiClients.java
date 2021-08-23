package client.bots;

import gui.dontneed.EmptyGUI;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.ServerException;
import org.example.models.players.SmartBot;
import org.example.models.strategies.RandomStrategy;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MultiClients {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8070;

    public static void main(final String[] args) {
        try {
            final List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                final Client botClient = new Client(IP, PORT, new SmartBot("bot" + i, new RandomStrategy()), new EmptyGUI());
                threadList.add(botClient);
                botClient.start();
            }

            for (final Thread th : threadList) {
                th.join();
            }
        } catch (final InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
