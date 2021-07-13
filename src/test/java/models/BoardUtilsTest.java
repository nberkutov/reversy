package models;

import exception.GameException;
import models.base.Cell;

import java.util.HashMap;
import java.util.Map;

import static models.Board.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.fail;

public class BoardUtilsTest {

    // 0 - is empty; b - black; w - white;
    public static Board parse(String string) throws GameException {
        Board board = new Board();
        int x = 0;
        int y = 0;
        for (int i = 0; i < string.length(); i++) {
            Point point = new Point(x, y);
            switch (string.charAt(i)) {
                case '0': {
                    board.setCell(point, Cell.EMPTY);
                    break;
                }
                case 'b': {
                    board.setCell(point, Cell.BLACK);
                    break;
                }
                case 'w': {
                    board.setCell(point, Cell.WHITE);
                    break;
                }
                case ' ': {
                    ++i;
                    break;
                }
                default: {
                    throw new RuntimeException();
                }
            }
            x++;
            if (x >= BOARD_SIZE) {
                y++;
                x = 0;
            }
        }
        return board;
    }
}
