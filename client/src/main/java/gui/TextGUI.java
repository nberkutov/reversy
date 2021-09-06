package gui;

import exception.ServerException;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;

import java.util.HashMap;
import java.util.Map;

public class TextGUI implements GameGUI {
    private static final String BLACK_MOVE_MSG = "ХОД ЧЕРНЫХ";
    private static final String WHITE_MOVE_MSG = "ХОД БЕЛЫХ";
    private static final String GAME_END_MSG = "КОНЕЦ ИГРЫ";
    private static final String LABEL_BLACK = "ЧЕРНЫЕ: ";
    private static final String LABEL_WHITE = "БЕЛЫЕ: ";
    private static final String BLACK_WINS = "ПОБЕДА ЧЕРНЫХ!";
    private static final String WHITE_WINS = "ПОБЕДА БЕЛЫХ!";

    private final Map<Cell, String> tiles;

    public TextGUI() {
        tiles = new HashMap<>();
        tiles.put(Cell.EMPTY, "_");
        tiles.put(Cell.BLACK, "◯");
        tiles.put(Cell.WHITE, "⊛");
    }

    public void updateGUI(final GameBoard board, final GameState gameState, final String opponent) throws ServerException {
        final StringBuilder boardBuilder = new StringBuilder();
        switch (gameState) {
            case BLACK_MOVE:
                boardBuilder.append(BLACK_MOVE_MSG);
                break;
            case WHITE_MOVE:
                boardBuilder.append(WHITE_MOVE_MSG);
                break;
            case END:
                boardBuilder.append(GAME_END_MSG);
                break;
        }
        boardBuilder.append('\n');
        boardBuilder.append(LABEL_BLACK).append(board.getCountBlackCells()).append('\n');
        boardBuilder.append(LABEL_WHITE).append(board.getCountWhiteCells()).append('\n');
        boardBuilder.append("  ");
        for (int i = 1; i <= board.getSize(); i++) {
            boardBuilder.append(i).append(' ');
        }
        boardBuilder.append('\n');
        for (int i = 0; i < board.getSize(); i++) {
            boardBuilder.append(i + 1).append(' ');
            for (int j = 0; j < board.getSize(); j++) {
                boardBuilder.append(tiles.get(board.getCell(j, i))).append(" ");
            }
            boardBuilder.append("\n");
        }
        System.out.println(boardBuilder);

        if (gameState == GameState.END) {
            if (board.getCountBlackCells() > board.getCountWhiteCells()) {
                System.out.println(BLACK_WINS);
            } else {
                System.out.println(WHITE_WINS);
            }
        }
    }
}
