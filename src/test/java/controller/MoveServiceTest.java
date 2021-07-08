package controller;

import exception.GameException;
import models.Board;
import models.BoardUtilsTest;
import models.Point;
import models.base.Cell;
import org.junit.jupiter.api.Test;
import services.MoveService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveServiceTest {

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
        Board board = new Board(BoardUtilsTest.parserMapByString(s));
        MoveService moveService = new MoveService(board);

        List<Point> result = moveService.getCellInAllDirection(new Point(4, 6), Cell.WHITE);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getX(), 4);
        assertEquals(result.get(0).getY(), 2);
        assertEquals(moveService.getCellInAllDirection(new Point(6, 3), Cell.WHITE).size(), 1);
        assertEquals(moveService.getCellInAllDirection(new Point(7, 3), Cell.WHITE).size(), 0);
        assertEquals(moveService.getCellInAllDirection(new Point(7, 7), Cell.WHITE).size(), 0);
        assertEquals(moveService.getCellInAllDirection(new Point(0, 0), Cell.WHITE).size(), 0);
        assertEquals(moveService.getCellInAllDirection(new Point(2, 1), Cell.BLACK).size(), 1);
    }

    @Test
    void testMove(){

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
        Board board = new Board(BoardUtilsTest.parserMapByString(s));
        MoveService moveService = new MoveService(board);

        assertEquals(moveService.getAvailableMoves(Cell.WHITE).size(), 8);
        assertEquals(moveService.getAvailableMoves(Cell.BLACK).size(), 0);
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
        Board board = new Board(BoardUtilsTest.parserMapByString(s));
        MoveService moveService = new MoveService(board);

        List<Point> points = moveService.getAvailableMoves(Cell.WHITE);
        assertEquals(points.size(), 4);
        assertTrue(points.contains(new Point(4,2)));
        assertTrue(points.contains(new Point(5,3)));
        assertTrue(points.contains(new Point(3,5)));
        assertTrue(points.contains(new Point(2,4)));

    }

}