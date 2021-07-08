package services;

import exception.GameException;
import models.Board;
import models.base.Cell;

import java.util.HashMap;
import java.util.Map;

public class BoardGUI {
    private final Map<Cell, String> tiles;
    private final Board board;

    public BoardGUI(Board board) {
        this.board = board;
        tiles = new HashMap<>();
        tiles.put(Cell.EMPTY, "_");
        tiles.put(Cell.BLACK, "⊛");
        tiles.put(Cell.WHITE, "◯");
    }

    public String getStringRepresentation() throws GameException {
        StringBuilder boardBuilder = new StringBuilder();
        for (int i = 0; i < board.BOARD_SIZE; i++) {
            for (int j = 0; j < board.BOARD_SIZE; j++) {
                boardBuilder.append(tiles.get(board.getCell(j, i))).append(" ");
            }
            boardBuilder.append("\n");
        }
        return boardBuilder.toString();
    }
}
