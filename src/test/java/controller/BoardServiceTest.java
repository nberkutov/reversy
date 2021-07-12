package controller;

import exception.GameErrorCode;
import exception.GameException;
import models.*;
import models.base.Cell;
import models.base.PlayerColor;
import org.junit.jupiter.api.Test;
import services.BoardService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static models.Board.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    @Test
    void testGetCellInAllDirection() throws GameException {
        String s = ""
                + "00000000"
                + "000wb000"
                + "000bww00"
                + "000wbb00"
                + "0000bb00"
                + "0000b000"
                + "00000000"
                + "00000000";
        Board board = BoardUtilsTest.parserBoardByString(s);


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
                + "0bbbbbbw"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "wbbbbbbw";
        Board a = BoardUtilsTest.parserBoardByString(before);
        BoardService.makeMove(a, new Point(0, 0), Cell.WHITE);
        String after = ""
                + "wwwwwwww"
                + "wwbbbbbb"
                + "wbwbbbbb"
                + "wbbwbbbb"
                + "wbbbwbbb"
                + "wbbbbwbb"
                + "wbbbbbwb"
                + "wbbbbbbw";
        Board b = BoardUtilsTest.parserBoardByString(after);
        assertEquals(a, b);
    }

    @Test
    void testMove2() throws GameException {
        String before = ""
                + "wbbbwbbw"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbb0bbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "wbbbwbbw";
        Board a = BoardUtilsTest.parserBoardByString(before);

        BoardService.makeMove(a, new Point(4, 4), Cell.WHITE);
        String after = ""
                + "wbbbwbbw"
                + "bwbbwbbb"
                + "bbwbwbbb"
                + "bbbwwbbb"
                + "bbbbwbbb"
                + "bbbbwwbb"
                + "bbbbwbwb"
                + "wbbbwbbw";
        Board b = BoardUtilsTest.parserBoardByString(after);
        assertEquals(a, b);
    }

    @Test
    void testGetAvailableMoves2() throws GameException {
        String s = ""
                + "00000000"
                + "00000000"
                + "00bbb000"
                + "00bwb000"
                + "00bbb000"
                + "00000000"
                + "00000000"
                + "00000000";
        Board board = BoardUtilsTest.parserBoardByString(s);

        assertEquals(BoardService.getAvailableMoves(board, Cell.WHITE).size(), 8);
        assertEquals(BoardService.getAvailableMoves(board, Cell.BLACK).size(), 0);
    }

    @Test
    void testGetAvailableMoves() throws GameException {
        String s = ""
                + "00000000"
                + "00000000"
                + "00000000"
                + "000wb000"
                + "000bw000"
                + "00000000"
                + "00000000"
                + "00000000";
        Board board = BoardUtilsTest.parserBoardByString(s);

        List<Point> points = BoardService.getAvailableMoves(board, Cell.WHITE);
        assertEquals(points.size(), 4);
        assertTrue(points.contains(new Point(4, 2)));
        assertTrue(points.contains(new Point(5, 3)));
        assertTrue(points.contains(new Point(3, 5)));
        assertTrue(points.contains(new Point(2, 4)));
    }

    @Test
    void testIsPossibleMove() throws GameException {
        String s = ""
                + "00000000"
                + "00000000"
                + "00000000"
                + "000wb000"
                + "000bw000"
                + "00000000"
                + "00000000"
                + "00000000";
        Board board =  BoardUtilsTest.parserBoardByString(s);

        Player player = new Player();
        player.setColor(PlayerColor.WHITE);
        assertTrue(BoardService.hasPossibleMove(board, player));
        player.setColor(PlayerColor.BLACK);
        assertTrue(BoardService.hasPossibleMove(board, player));

        String two = ""
                + "00000000"
                + "00000000"
                + "00000000"
                + "00000000"
                + "00000000"
                + "00000000"
                + "00000000"
                + "00000000";
        Board bs = BoardUtilsTest.parserBoardByString(two);
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
        //TODO: need edit on JUNIT ARRAYS
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
                + "00000000"
                + "0wb00000"
                + "0bw00000"
                + "00000000"
                + "00000000"
                + "00000000"
                + "00000000"
                + "00000000";
        Board board = BoardUtilsTest.parserBoardByString(before);

        try {
            BoardService.makeMove(board, new Point(1, 7), Cell.WHITE);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MOVE);
        }

        assertEquals(board, BoardUtilsTest.parserBoardByString(before));
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

}