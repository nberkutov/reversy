package org.example.services;

import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.logic.BoardUtils;
import org.example.models.base.Cell;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Board;
import org.example.models.board.Point;
import org.example.models.player.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.models.GameProperties.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.*;

class BoardLogicTest {

    @Test
    void testGetCellInAllDirection() throws ServerException {
        final String s = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 w b 0 0 0"
                + "0 0 0 b w w 0 0"
                + "0 0 0 w b b 0 0"
                + "0 0 0 0 b b 0 0"
                + "0 0 0 0 b 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        final GameBoard board = BoardUtils.fromString(s);


        final List<Point> result = BoardLogic.getCellInAllDirection(board, new Point(4, 6), Cell.WHITE);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getX(), 4);
        assertEquals(result.get(0).getY(), 2);
        assertEquals(BoardLogic.getCellInAllDirection(board, new Point(6, 3), Cell.WHITE).size(), 1);
        assertEquals(BoardLogic.getCellInAllDirection(board, new Point(7, 3), Cell.WHITE).size(), 0);
        assertEquals(BoardLogic.getCellInAllDirection(board, new Point(7, 7), Cell.WHITE).size(), 0);
        assertEquals(BoardLogic.getCellInAllDirection(board, new Point(0, 0), Cell.WHITE).size(), 0);
        assertEquals(BoardLogic.getCellInAllDirection(board, new Point(2, 1), Cell.BLACK).size(), 1);
    }

    @Test
    void testMove() throws ServerException {
        final String before = ""
                + "0 b b b b b b w"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "w b b b b b b w";
        final GameBoard a = BoardUtils.fromString(before);
        BoardLogic.makeMove(a, new Point(0, 0), Cell.WHITE);
        final String after = ""
                + "w w w w w w w w"
                + "w w b b b b b b"
                + "w b w b b b b b"
                + "w b b w b b b b"
                + "w b b b w b b b"
                + "w b b b b w b b"
                + "w b b b b b w b"
                + "w b b b b b b w";
        final GameBoard b = BoardUtils.fromString(after);
        assertEquals(a, b);
    }

    @Test
    void testMove2() throws ServerException {
        final String before = ""
                + "w b b b w b b w"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b 0 b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "w b b b w b b w";
        final GameBoard a = BoardUtils.fromString(before);

        BoardLogic.makeMove(a, new Point(4, 4), Cell.WHITE);
        final String after = ""
                + "w b b b w b b w"
                + "b w b b w b b b"
                + "b b w b w b b b"
                + "b b b w w b b b"
                + "b b b b w b b b"
                + "b b b b w w b b"
                + "b b b b w b w b"
                + "w b b b w b b w";
        final GameBoard b = BoardUtils.fromString(after);
        assertEquals(a, b);
    }

    @Test
    void testGetAvailableMoves2() throws ServerException {
        final String boardStr = ""
                + "00000000"
                + "00000000"
                + "00bbb000"
                + "00bwb000"
                + "00bbb000"
                + "00000000"
                + "00000000"
                + "00000000";
        final GameBoard board = BoardUtils.fromString(boardStr);

        assertEquals(8, BoardLogic.getAvailableMoves(board, Cell.WHITE).size());
        assertEquals(BoardLogic.getAvailableMoves(board, Cell.BLACK).size(), 0);
    }

    @Test
    void testGetAvailableMoves() throws ServerException {
        final String s = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 w b 0 0 0"
                + "0 0 0 b w 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        final GameBoard board = BoardUtils.fromString(s);

        final List<Point> points = BoardLogic.getAvailableMoves(board, Cell.WHITE);
        assertEquals(points.size(), 4);
        assertTrue(points.contains(new Point(4, 2)));
        assertTrue(points.contains(new Point(5, 3)));
        assertTrue(points.contains(new Point(3, 5)));
        assertTrue(points.contains(new Point(2, 4)));
    }

    @Test
    void testIsPossibleMove() throws ServerException {
        final String boardStr = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 w b 0 0 0"
                + "0 0 0 b w 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        final GameBoard board = BoardUtils.fromString(boardStr);

        final User user = new User("bot");
        user.setColor(PlayerColor.WHITE);
        assertTrue(BoardLogic.canMove(board, user.getColor()));
        user.setColor(PlayerColor.BLACK);
        assertTrue(BoardLogic.canMove(board, user.getColor()));

        final String two = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        final GameBoard bs = BoardUtils.fromString(two);
        assertFalse(BoardLogic.canMove(bs, user.getColor()));
        user.setColor(PlayerColor.WHITE);
        assertFalse(BoardLogic.canMove(bs, user.getColor()));
    }

    @Test
    void testIsPossibleMoveException() {
        final GameBoard board = new Board();

        try {
            BoardLogic.canMove(board, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

    @Test
    void testGetCellInAllDirectionException() {
        final GameBoard board = new Board();

        try {
            BoardLogic.getCellInAllDirection(board, null, Cell.WHITE);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.BAD_POINT);
        }

        try {
            BoardLogic.getCellInAllDirection(board, new Point(0, 0), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }

        try {
            BoardLogic.getCellInAllDirection(board, new Point(0, 0), Cell.EMPTY);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }

        final List<Point> forCheck = new ArrayList<>(Arrays.asList(
                new Point(-1, -1),
                new Point(-1, 0),
                new Point(0, -1),
                new Point(BOARD_SIZE + 1, 0),
                new Point(0, BOARD_SIZE + 1),
                new Point(BOARD_SIZE + 1, BOARD_SIZE + 1)));
        for (final Point p : forCheck) {
            try {
                BoardLogic.getCellInAllDirection(board, p, Cell.WHITE);
                fail();
            } catch (final ServerException e) {
                assertEquals(e.getErrorCode(), GameErrorCode.BAD_POINT);
            }
        }
    }

    @Test
    void testMoveException() throws ServerException {
        final String before = ""
                + "0 0 0 0 0 0 0 0"
                + "0 w b 0 0 0 0 0"
                + "0 b w 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        final GameBoard board = BoardUtils.fromString(before);

        try {
            BoardLogic.makeMove(board, new Point(1, 7), Cell.WHITE);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MOVE);
        }

        assertEquals(board, BoardUtils.fromString(before));
    }

    @Test
    void testGetAvailableMovesException() {
        final GameBoard board = new Board();

        try {
            BoardLogic.getAvailableMoves(board, (Cell) null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }
        try {
            BoardLogic.getAvailableMoves(board, Cell.EMPTY);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }
        try {
            BoardLogic.getAvailableMoves(board, (PlayerColor) null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

}