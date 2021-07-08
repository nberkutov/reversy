package controller;

import exception.GameErrorCode;
import exception.GameException;
import models.Board;
import models.BoardUtilsTest;
import models.Point;
import models.base.Cell;
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
        BoardService boardService = new BoardService(board);

        List<Point> result = boardService.getCellInAllDirection(new Point(4, 6), Cell.WHITE);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getX(), 4);
        assertEquals(result.get(0).getY(), 2);
        assertEquals(boardService.getCellInAllDirection(new Point(6, 3), Cell.WHITE).size(), 1);
        assertEquals(boardService.getCellInAllDirection(new Point(7, 3), Cell.WHITE).size(), 0);
        assertEquals(boardService.getCellInAllDirection(new Point(7, 7), Cell.WHITE).size(), 0);
        assertEquals(boardService.getCellInAllDirection(new Point(0, 0), Cell.WHITE).size(), 0);
        assertEquals(boardService.getCellInAllDirection(new Point(2, 1), Cell.BLACK).size(), 1);
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
        Board board = BoardUtilsTest.parserBoardByString(before);
        BoardService boardService = new BoardService(board);
        boardService.makeMove(new Point(0, 0), Cell.WHITE);
        String after = ""
                + "wwwwwwww"
                + "wwbbbbbb"
                + "wbwbbbbb"
                + "wbbwbbbb"
                + "wbbbwbbb"
                + "wbbbbwbb"
                + "wbbbbbwb"
                + "wbbbbbbw";
        Board a = boardService.getBoard();
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
        Board board = BoardUtilsTest.parserBoardByString(before);
        BoardService boardService = new BoardService(board);
        boardService.makeMove(new Point(4, 4), Cell.WHITE);
        String after = ""
                + "wbbbwbbw"
                + "bwbbwbbb"
                + "bbwbwbbb"
                + "bbbwwbbb"
                + "bbbbwbbb"
                + "bbbbwwbb"
                + "bbbbwbwb"
                + "wbbbwbbw";
        Board a = boardService.getBoard();
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
        BoardService boardService = new BoardService(board);

        assertEquals(boardService.getAvailableMoves(Cell.WHITE).size(), 8);
        assertEquals(boardService.getAvailableMoves(Cell.BLACK).size(), 0);
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
        BoardService boardService = new BoardService(board);

        List<Point> points = boardService.getAvailableMoves(Cell.WHITE);
        assertEquals(points.size(), 4);
        assertTrue(points.contains(new Point(4, 2)));
        assertTrue(points.contains(new Point(5, 3)));
        assertTrue(points.contains(new Point(3, 5)));
        assertTrue(points.contains(new Point(2, 4)));
    }

    @Test
    void testGetCellInAllDirectionException() throws GameException {
        Board board = new Board();
        BoardService boardService = new BoardService(board);

        try {
            boardService.getCellInAllDirection(null, Cell.WHITE);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.BAD_POINT);
        }

        try {
            boardService.getCellInAllDirection(new Point(0, 0), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }

        try {
            boardService.getCellInAllDirection(new Point(0, 0), Cell.EMPTY);
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
                boardService.getCellInAllDirection(p, Cell.WHITE);
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
        BoardService boardService = new BoardService(board);
        try {
            boardService.makeMove(new Point(1, 7), Cell.WHITE);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MOVE);
        }

        assertEquals(board, BoardUtilsTest.parserBoardByString(before));
    }

    @Test
    void testGetAvailableMovesException() throws GameException {
        Board board = new Board();
        BoardService boardService = new BoardService(board);

        try {
            boardService.getAvailableMoves((Cell) null);
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_CELL);
        }

    }

}