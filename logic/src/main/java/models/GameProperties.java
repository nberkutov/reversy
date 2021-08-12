package models;

import java.io.File;
import java.nio.file.Paths;

public class GameProperties {

    public static final String PROJECT_PATH = Paths.get("").toAbsolutePath().toString();

    public static final int BOARD_SIZE = 8;

    public static final int MIN_SIZE_NICKNAME = 3;
    public static final int MAX_SIZE_NICKNAME = 10;

    public static final int PORT = 8070;
    public static final String SERVER_FILE = PROJECT_PATH + File.separator + "server.dat";
    public static final String PLAYERS_FILE = PROJECT_PATH + File.separator + "statistic.csv";
    public static final String NEURAL_FILE = PROJECT_PATH + File.separator + "neural.eg";

    private GameProperties() {
    }

}
