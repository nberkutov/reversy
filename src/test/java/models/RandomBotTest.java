package models;

import controller.BoardController;
import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomBotTest {

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
        BoardController boardController = new BoardController(board);

        RandomBot bot = new RandomBot(0, PlayerColor.WHITE, boardController);
        List<Point> result = bot.getCellInAllDirection(new Point(4, 6), Cell.WHITE);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getX(), 4);
        assertEquals(result.get(0).getY(), 2);
        assertEquals(bot.getCellInAllDirection(new Point(6, 3), Cell.WHITE).size(), 1);
        assertEquals(bot.getCellInAllDirection(new Point(7, 3), Cell.WHITE).size(), 0);
        assertEquals(bot.getCellInAllDirection(new Point(7, 7), Cell.WHITE).size(), 0);
        assertEquals(bot.getCellInAllDirection(new Point(0, 0), Cell.WHITE).size(), 0);
        assertEquals(bot.getCellInAllDirection(new Point(2, 1), Cell.BLACK).size(), 1);
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
        BoardController boardController = new BoardController(board);

        RandomBot bot = new RandomBot(0, PlayerColor.WHITE, boardController);
        List<Point> points = bot.getAvailableMoves(bot.getColor());
        assertEquals(points.size(), 4);
        assertTrue(points.contains(new Point(4,2)));
        assertTrue(points.contains(new Point(5,3)));
        assertTrue(points.contains(new Point(3,5)));
        assertTrue(points.contains(new Point(2,4)));

    }
}