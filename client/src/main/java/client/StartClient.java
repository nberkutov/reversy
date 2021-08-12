package client;

import exception.ServerException;
import gui.EmptyGUI;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.players.AdaptiveNeuralBot;
import models.players.SmartBot;
import models.strategies.MyStrategy;
import models.strategies.algorithms.HardAlgorithm;


@Slf4j
public class StartClient {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8070;

    public static void main(String[] args) {
        try {
            final Client botClient = new Client(IP, PORT, new AdaptiveNeuralBot("neural"), new WindowGUI());
            final Client humanClient = new Client(IP, PORT, new SmartBot("mybot", new MyStrategy(4, new HardAlgorithm())), new EmptyGUI());
            botClient.start();
            humanClient.start();
            botClient.join();
            humanClient.join();
        } catch (final InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
        }
    }

}
