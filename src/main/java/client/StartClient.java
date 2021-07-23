package client;

import exception.GameException;
import gui.TextGUI;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.player.HumanConsolePayer;
import models.player.RandomBotPlayer;

@Slf4j
public class StartClient {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8081;

    public static void main(String[] args) {
        try {
            Client botClient = new Client(IP, PORT, new RandomBotPlayer(0, "bot1"), new WindowGUI());
            Client humanClient = new Client(IP, PORT, new HumanConsolePayer(1, "player1"), new TextGUI());
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
