package models;

import exception.GameErrorCode;
import exception.GameException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    static final int SIZE = 8;

    @Test
    void testGetCell() throws GameException {
        GameBoard board = new Board();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                assertNotNull(board.getCell(x, y));
                assertNotNull(board.getCell(new Point(x, y)));
            }
        }
    }

    @Test
    void testCreateBoard() throws GameException {
        GameBoard board = new Board();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (y == 3 && x == 3 || y == 4 && x == 4) {
                    assertEquals(Cell.WHITE, board.getCell(new Point(x, y)));
                } else if (y == 3 && x == 4 || y == 4 && x == 3) {
                    assertEquals(Cell.BLACK, board.getCell(new Point(x, y)));
                } else {
                    assertEquals(Cell.EMPTY, board.getCell(new Point(x, y)));
                }
            }
        }
    }

    @Test
    void testSetCell() throws GameException {
        GameBoard board = new Board();
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        board.setCell(0, 0, Cell.BLACK);
        assertEquals(Cell.BLACK, board.getCell(0, 0));
        board.setCell(0, 0, Cell.WHITE);
        assertEquals(Cell.WHITE, board.getCell(0, 0));
        board.setCell(0, 0, Cell.EMPTY);
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
    }

    @Test
    void getCellInAllDirection() throws GameException {
        String s = ""
                + "00000000"
                + "000wb000"
                + "000bww00"
                + "000wbb00"
                + "0000bb00"
                + "0000b000"
                + "00000000"
                + "00000000";
        GameBoard board = new Board(parserMapByString(s));
        List<Point> result = board.getCellInAllDirection(new Point(4, 6), Cell.WHITE);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getX(), 4);
        assertEquals(result.get(0).getY(), 2);
        assertEquals(board.getCellInAllDirection(new Point(6, 3), Cell.WHITE).size(),2);
        assertEquals(board.getCellInAllDirection(new Point(7, 3), Cell.WHITE).size(),0);
        assertEquals(board.getCellInAllDirection(new Point(7, 7), Cell.WHITE).size(),0);
        assertEquals(board.getCellInAllDirection(new Point(0, 0), Cell.WHITE).size(),0);
        assertEquals(board.getCellInAllDirection(new Point(2, 1), Cell.BLACK).size(),2);
    }

    // 0 - is empty; b - black; w - white;
    private Map<Point, Cell> parserMapByString(String string) {
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
            if (x >= SIZE) {
                y++;
                x = 0;
            }
        }
        return map;
    }

    @Test
    void getCountCell() throws GameException {
        Cell firstCheck = Cell.BLACK;
        Cell secondCheck = Cell.WHITE;
        Map<Point, Cell> map = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                map.put(new Point(i, j), firstCheck);
            }
        }
        GameBoard board = new Board(map);
        assertEquals(board.getCountCell(firstCheck), SIZE * SIZE);
        assertEquals(board.getCountCell(secondCheck), 0);
        Random random = new Random();
        board.setCell(random.nextInt(SIZE), random.nextInt(SIZE), secondCheck);
        assertEquals(board.getCountCell(firstCheck), SIZE * SIZE - 1);
        assertEquals(board.getCountCell(secondCheck), 1);

        GameBoard classicBoard = new Board();
        assertEquals(classicBoard.getCountCell(firstCheck), 2);
        assertEquals(classicBoard.getCountCell(secondCheck), 2);
    }

    @Test
    void testReverseCell() throws GameException {
        GameBoard board = new Board();
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        try {
            board.reverseCell(0, 0);
            fail();
        } catch (GameException ex) {
            assertEquals(ex.getErrorCode(), GameErrorCode.CELL_IS_EMPTY);
        }
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        board.setCell(0, 0, Cell.BLACK);
        board.reverseCell(0, 0);
        assertEquals(Cell.WHITE, board.getCell(0, 0));
        board.reverseCell(0, 0);
        assertEquals(Cell.BLACK, board.getCell(0, 0));
    }
}