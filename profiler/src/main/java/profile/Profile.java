package profile;

import models.base.GameState;
import models.base.PlayerColor;

import java.io.Serializable;
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
        } else {
        final Map<String, Integer> map = freq.get(initialBoard);
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
        return freq.get(initialBoard).getOrDefault(nextBoard, 0);
    }
}
