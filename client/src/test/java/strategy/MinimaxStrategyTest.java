package strategy;

import exception.ServerException;
import logic.BoardLogic;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.ArrayBoard;
import models.board.Point;
import org.junit.jupiter.api.Test;
import player.Player;
import selfplay.BotPlayer;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxStrategyTest {

    @Test
    void testMinimax() {
        final Player minimaxPlayer = new BotPlayer("minimax", new MinimaxStrategy(3, Utility::advanced));
        final Player randomBot = new BotPlayer("random", new RandomStrategy());

        minimaxPlayer.setColor(PlayerColor.WHITE);
        randomBot.setColor(PlayerColor.BLACK);

        final GameBoard board = new ArrayBoard("wwwwwwwbwbwwbwwwwwbwwbwbwwbwbwwbwwwbwwwbwwbbbwwewbwwwbwebbwbbbbe");
        try {
            Point move = minimaxPlayer.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(minimaxPlayer.getColor()));

            move = randomBot.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(randomBot.getColor()));

            move = minimaxPlayer.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(minimaxPlayer.getColor()));

            assertTrue(board.getCountWhiteCells() > board.getCountBlackCells());
        } catch (final ServerException e) {
            e.printStackTrace();
        }
    }

}