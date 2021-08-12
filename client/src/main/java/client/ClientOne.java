package client;

import exception.ServerException;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.players.AdaptiveNeuralBot;

@Slf4j
public class ClientOne {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8070;

    public static void main(final String[] args) {
        try {
            final Client botClient = new Client(IP, PORT, new AdaptiveNeuralBot("neural"), new WindowGUI());
            botClient.start();
            botClient.join();
        } catch (final InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
        }
    }
}
