
import exception.ServerException;
import gui.EmptyGUI;
import gui.WindowGUI;
import lombok.extern.slf4j.Slf4j;
import models.base.SmartBot;
import models.strategies.NeuralStrategy;
import models.strategies.RandomStrategy;


@Slf4j
public class StartClient {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8070;

    public static void main(String[] args) {
        try {
            Client botClient = new Client(IP, PORT, new SmartBot("minimax", new NeuralStrategy(true)), new WindowGUI());
            Client humanClient = new Client(IP, PORT, new SmartBot("Random", new RandomStrategy()), new EmptyGUI());
            Thread thread1 = new Thread(botClient);
            Thread thread2 = new Thread(humanClient);
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
        } catch (InterruptedException | ServerException e) {
            log.error("ERROR {}", e.getMessage());
        }
    }

}
