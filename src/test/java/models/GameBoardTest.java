package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    static final int SIZE = 8;

    @Test
    void testGetCell() {
        GameBoard board = new Board();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                assertNotNull(board.getCell(x, y));
                assertNotNull(board.getCell(new Point(x, y)));
            }
        }
    }

    @Test
    void testCreateBoard() {
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
    void testSetCell() {
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
    void getCellInAllDirection() {
    }

    @Test
    void getCountCell() {
    }

    @Test
    void testReverseCell() {
        GameBoard board = new Board();
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        try {
            board.reverseCell(0, 0);
            fail();
        } catch (RuntimeException ignored){}
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        board.setCell(0, 0, Cell.BLACK);
        board.reverseCell(0, 0);
        assertEquals(Cell.WHITE, board.getCell(0, 0));
        board.reverseCell(0, 0);
        assertEquals(Cell.BLACK, board.getCell(0, 0));
    }
}