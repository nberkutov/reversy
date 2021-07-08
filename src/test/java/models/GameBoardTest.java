package models;

import exception.GameErrorCode;
import exception.GameException;
import models.base.Cell;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    static final int SIZE = 8;

    @Test
    void testGetCell() throws GameException {
        Board board = new Board();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                assertNotNull(board.getCell(x, y));
                assertNotNull(board.getCell(new Point(x, y)));
            }
        }
    }

    @Test
    void testCreateBoard() throws GameException {
        Board board = new Board();
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
        Board board = new Board();
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        board.setCell(0, 0, Cell.BLACK);
        assertEquals(Cell.BLACK, board.getCell(0, 0));
        board.setCell(0, 0, Cell.WHITE);
        assertEquals(Cell.WHITE, board.getCell(0, 0));
        board.setCell(0, 0, Cell.EMPTY);
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
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
        Board board = new Board(map);
        assertEquals(board.getCountCell(firstCheck), SIZE * SIZE);
        assertEquals(board.getCountCell(secondCheck), 0);
        Random random = new Random();
        board.setCell(random.nextInt(SIZE), random.nextInt(SIZE), secondCheck);
        assertEquals(board.getCountCell(firstCheck), SIZE * SIZE - 1);
        assertEquals(board.getCountCell(secondCheck), 1);

        Board classicBoard = new Board();
        assertEquals(classicBoard.getCountCell(firstCheck), 2);
        assertEquals(classicBoard.getCountCell(secondCheck), 2);
    }
}