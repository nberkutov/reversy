import exception.ServerException;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.base.SmartBot;
import models.strategies.MinimaxStrategy;
import models.strategies.algorithms.StrangeAlgorithm;

@Slf4j
public class ClientOne {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8070;

    public static void main(String[] args) {
        try {
            Client botClient = new Client(IP, PORT, new SmartBot("minimax", new MinimaxStrategy(2, new StrangeAlgorithm())), new WindowGUI());
            Thread thread1 = new Thread(botClient);
            thread1.start();
            thread1.join();
        } catch (InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
        }
    }
}
