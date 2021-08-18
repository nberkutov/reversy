package client;

import exception.ServerException;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.players.SmartBot;
import models.strategies.RandomStrategy;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MultiClients {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            final List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                final Client botClient = new Client(IP, PORT, new SmartBot("bot" + i, new RandomStrategy()), new WindowGUI());
                threadList.add(botClient);
                botClient.start();
            }

            for (final Thread th : threadList) {
                th.join();
            }
        } catch (InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
        }
    }
}
