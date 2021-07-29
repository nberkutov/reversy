package client;

import client.models.OneStepBotPlayer;
import client.models.RandomBotPlayer;
import exception.GameException;
import gui.NoGUI;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class StartClient {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8081;

    public static void main(String[] args) {
        try {
            Client botClient = new Client(IP, PORT, new RandomBotPlayer("bot1"), new NoGUI());
            Client humanClient = new Client(IP, PORT, new OneStepBotPlayer("OneStepAheadBot"), new NoGUI());
            Thread thread1 = new Thread(botClient);
            Thread thread2 = new Thread(humanClient);
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
        } catch (InterruptedException | GameException e) {
            log.error("ERROR {}", e.getMessage());
        }
    }

}
