package profile;

import models.base.GameState;
import models.base.PlayerColor;
import parser.LogParser;

import java.util.Map;


public class Profile {
    private final Map<String, Map<String, Integer>> blackMovesFreq;
    private final Map<String, Map<String, Integer>> whiteMovesFreq;
    private GameState opponentState;
    private GameState playerState;

    public static void main(final String[] args) {
        final LogParser logParser = new LogParser();
        final Profile profile = logParser.parse();
    }

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

    public int getProbability(final String initialBoard, final String nextBoard) {
        final Map<String, Map<String, Integer>> freq;
        if (opponentState == GameState.BLACK_MOVE) {
            freq = blackMovesFreq;
        } else {
            freq = whiteMovesFreq;
        }

        final Map<String, Integer> next = freq.get(initialBoard);
        if (next != null) {
            return next.getOrDefault(nextBoard, 0);
        }
        return 0;
    }
}
