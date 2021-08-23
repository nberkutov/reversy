package org.example.logic;

import org.example.exception.ServerException;
import org.example.models.base.Cell;
import org.example.models.base.interfaces.GameBoard;


public class BoardUtils {

    private BoardUtils() {
    }

    public static String toString(final GameBoard board) throws ServerException {
        final StringBuilder boardBuilder = new StringBuilder();
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                char c = '0';
                switch (board.getCell(x, y)) {
                    case EMPTY:
                        break;
                    case WHITE:
                        c = 'w';
                        break;
                    case BLACK:
                        c = 'b';
                        break;
                }
                boardBuilder.append(c);
            }
        }
        return boardBuilder.toString();
    }

    public static GameBoard fromString(final String string) throws ServerException {
        final GameBoard board = BoardFactory.generateStartedBoard();
        board.setTextCells(string);
        updateCellsByTextCells(board);
        return board;
    }

    public static void updateCellsByTextCells(final GameBoard board) {
        final String string = board.getTextCells();
        int k = 0;
        for (int i = 0; i < string.length(); i++) {
            final int x = k % board.getSize();
            final int y = k / board.getSize();
            switch (string.charAt(i)) {
                case '0':
                    board.setCell(x, y, Cell.EMPTY);
                    k++;
                    break;
                case 'b':
                    board.setCell(x, y, Cell.BLACK);
                    k++;
                    break;
                case 'w':
                    board.setCell(x, y, Cell.WHITE);
                    k++;
                    break;
                case ' ':
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }

}
