package org.example.models;

import org.example.models.board.BoardDefault;

import java.io.File;
import java.nio.file.Paths;

public class GameProperties {

    public static final String PROJECT_PATH = Paths.get("").toAbsolutePath().toString();

    public static final int BOARD_SIZE = 8;
    public static final BoardDefault boardDefault = BoardDefault.ARRAY_BOARD;

    public static final int MIN_SIZE_NICKNAME = 3;
    public static final int MAX_SIZE_NICKNAME = 10;

    public static final int PORT = 8070;
    public static final String NEURAL_FILE = PROJECT_PATH + File.separator + "neural.eg";
    public static final int CLIENT_THREADS = 10000;
    public static final int HANDLER_THREADS = 4;
    public static final int GAME_SEARCH_THREADS = 1;

    private GameProperties() {
    }

}
