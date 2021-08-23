package gui.dontneed;

import gui.GameGUI;
import org.example.exception.ServerException;
import org.example.models.base.Cell;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;

import java.util.HashMap;
import java.util.Map;

public class TextGUI implements GameGUI {
    private final Map<Cell, String> tiles;

    public TextGUI() {
        tiles = new HashMap<>();
        tiles.put(Cell.EMPTY, "_");
        tiles.put(Cell.BLACK, "◯");
        tiles.put(Cell.WHITE, "⊛");
    }

    @Override
    public void setTitle(String title) {

    }

    public void updateGUI(final GameBoard board, final GameState gameState) throws ServerException {
        final StringBuilder boardBuilder = new StringBuilder();
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
        boardBuilder.append('\n');
        boardBuilder.append("ЧЕРНЫЕ: ").append(board.getCountBlackCells()).append('\n');
        boardBuilder.append("БЕЛЫЕ: ").append(board.getCountWhiteCells()).append('\n');
        boardBuilder.append("  ");
        for (int i = 1; i <= 8; i++) {
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
                System.out.println("ПОБЕДА ЧЕРНЫХ!");
            } else {
                System.out.println("ПОБЕДА БЕЛЫХ!");
            }
        }
    }

    @Override
    public void setSimpleCloseByWindow(boolean bool) {

    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
