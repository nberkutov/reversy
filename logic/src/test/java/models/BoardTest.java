package models;

import exception.GameErrorCode;
import exception.ServerException;
import models.base.Cell;
import models.base.interfaces.GameBoard;
import models.board.Point;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static models.GameProperties.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest extends BaseAllBoards {

    @ParameterizedTest
    @MethodSource("getAllBoards")
    void testGetCell(final GameBoard board) throws ServerException {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                assertNotNull(board.getCell(x, y));
                assertNotNull(board.getCell(new Point(x, y)));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("getAllBoards")
    void testCreateBoard(final GameBoard board) throws ServerException {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
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

    @ParameterizedTest
    @MethodSource("getAllBoards")
    void testSetCell(final GameBoard board) throws ServerException {
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        board.setCell(0, 0, Cell.BLACK);
        assertEquals(Cell.BLACK, board.getCell(0, 0));
        board.setCell(0, 0, Cell.WHITE);
        assertEquals(Cell.WHITE, board.getCell(0, 0));
        board.setCell(0, 0, Cell.EMPTY);
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
    }

    @ParameterizedTest
    @MethodSource("getAllBoards")
    void testReverseCell(final GameBoard board) throws ServerException {
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        try {
            board.reverseCell(0, 0);
            fail();
        } catch (final ServerException ex) {
            assertEquals(ex.getErrorCode(), GameErrorCode.INVALID_CELL);
        }
        assertEquals(Cell.EMPTY, board.getCell(0, 0));
        board.setCell(0, 0, Cell.BLACK);
        board.reverseCell(0, 0);
        assertEquals(Cell.WHITE, board.getCell(0, 0));
        board.reverseCell(0, 0);
        assertEquals(Cell.BLACK, board.getCell(0, 0));
    }
}
