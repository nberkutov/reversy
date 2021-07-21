package gui;

import exception.GameException;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TextGUI implements GameGUI {
    private final Map<Cell, String> tiles;
    private BiConsumer<Integer, Integer> callback;
    //private final GameBoard board;

    public TextGUI() {
        //this.board = board;
        tiles = new HashMap<>();
        tiles.put(Cell.EMPTY, "_");
        tiles.put(Cell.BLACK, "⊛");
        tiles.put(Cell.WHITE, "◯");
    }

    public void updateGUI(GameBoard board, GameState gameState) throws GameException {
        StringBuilder boardBuilder = new StringBuilder();
        switch (gameState) {
            case BLACK_MOVE:
                boardBuilder.append("ХОД ЧЕРНЫХ");
                break;
            case WHITE_MOVE:
                boardBuilder.append("ХОД БЕЛЫХ");
                break;
            case END:
                boardBuilder.append("КОНЕЦ ИГРЫ");
                break;
        }
        boardBuilder.append("ЧЕРНЫЕ: ").append(board.getCountBlackCells()).append('\n');
        boardBuilder.append("БЕЛЫЕ: ").append(board.getCountWhiteCells()).append('\n');
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                boardBuilder.append(tiles.get(board.getCell(j, i))).append(" ");
            }
            boardBuilder.append("\n");
        }
        System.out.println(boardBuilder);
    }
}
