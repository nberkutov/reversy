package services;

import exception.GameErrorCode;
import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.board.Board;
import models.board.Point;
import models.player.Player;
import models.player.RandomBotPlayer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static models.GameProperties.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    @Test
    void testGetCellInAllDirection() throws GameException {
        String s = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 w b 0 0 0"
                + "0 0 0 b w w 0 0"
                + "0 0 0 w b b 0 0"
                + "0 0 0 0 b b 0 0"
                + "0 0 0 0 b 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        Board board = BoardUtils.fromString(s);


        List<Point> result = BoardService.getCellInAllDirection(board, new Point(4, 6), Cell.WHITE);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getX(), 4);
        assertEquals(result.get(0).getY(), 2);
        assertEquals(BoardService.getCellInAllDirection(board, new Point(6, 3), Cell.WHITE).size(), 1);
        assertEquals(BoardService.getCellInAllDirection(board, new Point(7, 3), Cell.WHITE).size(), 0);
        assertEquals(BoardService.getCellInAllDirection(board, new Point(7, 7), Cell.WHITE).size(), 0);
        assertEquals(BoardService.getCellInAllDirection(board, new Point(0, 0), Cell.WHITE).size(), 0);
        assertEquals(BoardService.getCellInAllDirection(board, new Point(2, 1), Cell.BLACK).size(), 1);
    }

    @Test
    void testMove() throws GameException {
        String before = ""
                + "0 b b b b b b w"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "w b b b b b b w";
        Board a = BoardUtils.fromString(before);
        BoardService.makeMove(a, new Point(0, 0), Cell.WHITE);
        String after = ""
                + "w w w w w w w w"
                + "w w b b b b b b"
                + "w b w b b b b b"
                + "w b b w b b b b"
                + "w b b b w b b b"
                + "w b b b b w b b"
                + "w b b b b b w b"
                + "w b b b b b b w";
        Board b = BoardUtils.fromString(after);
        assertEquals(a, b);
    }

    @Test
    void testMove2() throws GameException {
        String before = ""
                + "w b b b w b b w"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "b b b b 0 b b b"
                + "b b b b b b b b"
                + "b b b b b b b b"
                + "w b b b w b b w";
        Board a = BoardUtils.fromString(before);

        BoardService.makeMove(a, new Point(4, 4), Cell.WHITE);
        String after = ""
                + "w b b b w b b w"
                + "b w b b w b b b"
                + "b b w b w b b b"
                + "b b b w w b b b"
                + "b b b b w b b b"
                + "b b b b w w b b"
                + "b b b b w b w b"
                + "w b b b w b b w";
        Board b = BoardUtils.fromString(after);
        assertEquals(a, b);
    }

    @Test
    void testGetAvailableMoves2() throws GameException {
        String boardStr = ""
                + "00000000"
                + "00000000"
                + "00bbb000"
                + "00bwb000"
                + "00bbb000"
                + "00000000"
                + "00000000"
                + "00000000";
        Board board = BoardUtils.fromString(boardStr);

        assertEquals(8, BoardService.getAvailableMoves(board, Cell.WHITE).size());
        assertEquals(BoardService.getAvailableMoves(board, Cell.BLACK).size(), 0);
    }

    @Test
    void testGetAvailableMoves() throws GameException {
        String s = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 w b 0 0 0"
                + "0 0 0 b w 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        Board board = BoardUtils.fromString(s);

        List<Point> points = BoardService.getAvailableMoves(board, Cell.WHITE);
        assertEquals(points.size(), 4);
        assertTrue(points.contains(new Point(4, 2)));
        assertTrue(points.contains(new Point(5, 3)));
        assertTrue(points.contains(new Point(3, 5)));
        assertTrue(points.contains(new Point(2, 4)));
    }

    @Test
    void testIsPossibleMove() throws GameException {
        String boardStr = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 w b 0 0 0"
                + "0 0 0 b w 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        Board board = BoardUtils.fromString(boardStr);

        Player player = new RandomBotPlayer();
        player.setColor(PlayerColor.WHITE);
        assertTrue(BoardService.hasPossibleMove(board, player));
        player.setColor(PlayerColor.BLACK);
        assertTrue(BoardService.hasPossibleMove(board, player));

        String two = ""
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        Board bs = BoardUtils.fromString(two);
        assertFalse(BoardService.hasPossibleMove(bs, player));
        player.setColor(PlayerColor.WHITE);
        assertFalse(BoardService.hasPossibleMove(bs, player));
    }

    @Test
    void testIsPossibleMoveException() {
        Board board = new Board();

        try {
            BoardService.hasPossibleMove(board, null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

    @Test
    void testGetCellInAllDirectionException() {
        Board board = new Board();

        try {
            BoardService.getCellInAllDirection(board, null, Cell.WHITE);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.BAD_POINT);
        }

        try {
            BoardService.getCellInAllDirection(board, new Point(0, 0), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }

        try {
            BoardService.getCellInAllDirection(board, new Point(0, 0), Cell.EMPTY);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }

        List<Point> forCheck = new ArrayList<>(Arrays.asList(
                new Point(-1, -1),
                new Point(-1, 0),
                new Point(0, -1),
                new Point(BOARD_SIZE + 1, 0),
                new Point(0, BOARD_SIZE + 1),
                new Point(BOARD_SIZE + 1, BOARD_SIZE + 1)));
        for (Point p : forCheck) {
            try {
                BoardService.getCellInAllDirection(board, p, Cell.WHITE);
                fail();
            } catch (GameException e) {
                assertEquals(e.getErrorCode(), GameErrorCode.BAD_POINT);
            }
        }
    }

    @Test
    void testMoveException() throws GameException {
        String before = ""
                + "0 0 0 0 0 0 0 0"
                + "0 w b 0 0 0 0 0"
                + "0 b w 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0"
                + "0 0 0 0 0 0 0 0";
        Board board = BoardUtils.fromString(before);

        try {
            BoardService.makeMove(board, new Point(1, 7), Cell.WHITE);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MOVE);
        }

        assertEquals(board, BoardUtils.fromString(before));
    }

    @Test
    void testGetAvailableMovesException() {
        Board board = new Board();

        try {
            BoardService.getAvailableMoves(board, (Cell) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }
        try {
            BoardService.getAvailableMoves(board, Cell.EMPTY);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }
        try {
            BoardService.getAvailableMoves(board, (PlayerColor) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

    @Test
    void testGameEnd() {

    }

}