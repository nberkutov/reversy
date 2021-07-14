package models;

import exception.GameException;
import models.base.Cell;
import models.board.Board;

import static models.board.Board.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.fail;

public class BoardUtilsTest {

    // 0 - is empty; b - black; w - white;
    public static Board parse(String string) throws GameException {
        Board board = new Board();
        int k = 0;
        for (int i = 0; i < string.length(); i++) {
            int x = k % BOARD_SIZE;
            int y = k / BOARD_SIZE;
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
        return board;
    }
}
