package models;

import models.base.Cell;

import java.util.HashMap;
import java.util.Map;

import static models.Board.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.fail;

public class BoardUtilsTest {

    // 0 - is empty; b - black; w - white;
    public static Map<Point, Cell> parserMapByString(String string) {
        Map<Point, Cell> map = new HashMap<>();
        int x = 0;
        int y = 0;
        for (int i = 0; i < string.length(); i++) {
            Point point = new Point(x, y);
            switch (string.charAt(i)) {
                case '0': {
                    map.put(point, Cell.EMPTY);
                    break;
                }
                case 'b': {
                    map.put(point, Cell.BLACK);
                    break;
                }
                case 'w': {
                    map.put(point, Cell.WHITE);
                    break;
                }
                default: {
                    fail();
                }
            }
            x++;
            if (x >= BOARD_SIZE) {
                y++;
                x = 0;
            }
        }
        return map;
    }
}
