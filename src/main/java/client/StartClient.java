package client;

import client.models.MinimaxBotPlayer;
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
            Client client2 = new Client(IP, PORT, new RandomBotPlayer("RandomBot"), new NoGUI());
            Client client1 = new Client(IP, PORT, new MinimaxBotPlayer("MinimaxBot"), new WindowGUI());
            Thread thread1 = new Thread(client1);
            Thread thread2 = new Thread(client2);
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
        } catch (InterruptedException | GameException e) {
            log.error("ERROR {}", e.getMessage());
        }
    }

}
