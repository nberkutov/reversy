package parser;

import models.base.GameState;
import profile.Profile;
import strategy.ArrayBoard;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LogParser {
    public final Map<String, Map<String, Integer>> blackMovesFreq;
    private final Map<String, Map<String, Integer>> whiteMovesFreq;
    private GameState opponentState;
    private GameState playerState;

    private String prevLine;

    public LogParser() {
        blackMovesFreq = new HashMap<>();
        whiteMovesFreq = new HashMap<>();
        playerState = GameState.BLACK_MOVE;
        opponentState = GameState.WHITE_MOVE;
        prevLine = new ArrayBoard().toString();
    }

    public Profile parseProfile(final String path) {
        final File file = new File(path);
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new Profile(blackMovesFreq, whiteMovesFreq);
    }

    private void parseLine(final String line) {
        if (line.startsWith("color")) {
            setOpponentState(line);
        } else if (line.startsWith(opponentState.toString())) {
            handleInitialState(line);
            prevLine = line;
        } else if (line.startsWith(playerState.toString()) || line.startsWith(GameState.END.toString())) {
            handleTransition(line);
        }
    }

    private void setOpponentState(final String line) {
        final String[] splitted = line.split("=");
        try {
            if ("BLACK".equals(splitted[1])) {
                playerState = GameState.BLACK_MOVE;
                opponentState = GameState.WHITE_MOVE;
            } else {
                playerState = GameState.WHITE_MOVE;
                opponentState = GameState.BLACK_MOVE;
            }
        } catch (final ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Missed color value.");
        }
    }

    private void handleInitialState(final String line) {
        try {
            final Map<String, Map<String, Integer>> movesFreq;
            if (opponentState == GameState.BLACK_MOVE) {
                movesFreq = blackMovesFreq;
            } else {
                movesFreq = whiteMovesFreq;
            }
            movesFreq.putIfAbsent(parseBoard(line), new HashMap<>());
        } catch (final ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Board state missed.");
        }
    }

    private void handleTransition(final String line) {
        final Map<String, Map<String, Integer>> movesFreq;
        if (opponentState == GameState.BLACK_MOVE) {
            movesFreq = blackMovesFreq;
        } else {
            movesFreq = whiteMovesFreq;
        }
        if (prevLine.startsWith(opponentState.toString())) {
            final Map<String, Integer> map = movesFreq.get(parseBoard(prevLine));
            final String board = parseBoard(line);
            if (map.containsKey(board)) {
                final int x = map.get(board) + 1;
                map.put(board, x);
            } else {
                map.put(board, 1);
            }
        }
    }


    private String parseBoard(final String line) {
        final String[] splitted = line.split(" ");
        if (splitted.length < 2) {
            throw new IllegalArgumentException("Got invalid line to parse board.");
        }
        return splitted[1];
    }
}
