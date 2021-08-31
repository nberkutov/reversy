package profile;

import models.base.GameState;
import models.base.PlayerColor;
import parser.LogParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Profile implements Serializable {
    private final Map<String, Map<String, Integer>> blackMovesFreq;
    private final Map<String, Map<String, Integer>> whiteMovesFreq;
    private GameState opponentState;

    public Profile(
            final Map<String, Map<String, Integer>> blackMovesFreq, final Map<String, Map<String, Integer>> whiteMovesFreq) {
        this.blackMovesFreq = blackMovesFreq;
        this.whiteMovesFreq = whiteMovesFreq;
    }

    public void setOpponentState(final PlayerColor color) {
        if (color == PlayerColor.BLACK) {
            opponentState = GameState.BLACK_MOVE;
        } else {
            opponentState = GameState.WHITE_MOVE;
        }
    }

    public void addOrInc(final String initialBoard, final String nextBoard) {
        final Map<String, Map<String, Integer>> freq;
        if (opponentState == GameState.BLACK_MOVE) {
            freq = blackMovesFreq;
        } else {
            freq = whiteMovesFreq;
        }
        if (!freq.containsKey(initialBoard)) {
            freq.put(initialBoard, new HashMap<>());
            //System.out.println("ADD");
        } else {
        final Map<String, Integer> map = freq.get(initialBoard);
        //if (map.containsKey(nextBoard)) {
            //System.out.println("INC");
            map.put(nextBoard, map.getOrDefault(nextBoard, 0) + 1);
        }
    }

    public int getFrequency(final String initialBoard, final String nextBoard) {
        final Map<String, Map<String, Integer>> freq;
        if (opponentState == GameState.BLACK_MOVE) {
            freq = blackMovesFreq;
        } else {
            freq = whiteMovesFreq;
        }
        if (!freq.containsKey(initialBoard)) {
            freq.put(initialBoard, new HashMap<>());
        }
        final int frequency = freq.get(initialBoard).getOrDefault(nextBoard, 1);
        addOrInc(initialBoard, nextBoard);
        return frequency;
    }

    public static Profile parse(final String logPath) {
        final LogParser logParser = new LogParser();
        return logParser.parse(logPath);
    }

    public void save(final String fileName) throws Exception {
        try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
        } catch (final IOException e) {
            throw new Exception("Не удалось сохранить профиль в файл.");
        }
    }

    public static Profile fromFile(final String fileName) throws Exception {
        try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Profile) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            throw new Exception("Не удалось считать из профиль из файла.");
        }
    }
}
