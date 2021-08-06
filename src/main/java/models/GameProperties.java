package models;

public class GameProperties {

    public static final String PROJECT_PATH = System.getProperty("user.dir");

    public static final int BOARD_SIZE = 8;

    public static final int MIN_SIZE_NICKNAME = 3;
    public static final int MAX_SIZE_NICKNAME = 10;

    public static final int PORT = 8081;
    public static final String SERVER_FILE = PROJECT_PATH + "\\" + "server.dat";
    public static final String PLAYERS_FILE = PROJECT_PATH + "\\" + "statistic.csv";
    public static final String NEURAL_FILE = PROJECT_PATH + "\\" + "neural.ser";

    private GameProperties() {
    }

}
