package strategy;

import exception.ServerException;
import logic.BoardLogic;
import models.ArrayBoard;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import org.junit.jupiter.api.Test;
import player.Player;
import selfplay.BotPlayer;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxStrategyTest {

    @Test
    void testMinimaxWhiteDepth3() {
        final Player minimaxPlayer = new BotPlayer("minimax", new MinimaxStrategy(3, Utility::multiHeuristic));
        final Player randomBot = new BotPlayer("random", new RandomStrategy());

        minimaxPlayer.setColor(PlayerColor.WHITE);
        randomBot.setColor(PlayerColor.BLACK);

        final GameBoard board = new models.ArrayBoard("wwwwwwwbwbwwbwwwwwbwwbwbwwbwbwwbwwwbwwwbwwbbbwwewbwwwbwebbwbbbbe");

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

    @Test
    void testMinimaxBlackDepth3() {
        final Player minimaxPlayer = new BotPlayer("minimax", new MinimaxStrategy(3, Utility::multiHeuristic));
        final Player randomBot = new BotPlayer("random", new RandomStrategy());

        minimaxPlayer.setColor(PlayerColor.BLACK);
        randomBot.setColor(PlayerColor.WHITE);

        final GameBoard board = new ArrayBoard("bbwwewbbwbwwwwbbewbbbwbewbwwwwbwbbbwbwbwwbwbwwbwwbbbbwwbwbewewwe");

        try {
            Point move = minimaxPlayer.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(minimaxPlayer.getColor()));

            move = randomBot.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(randomBot.getColor()));

            move = minimaxPlayer.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(minimaxPlayer.getColor()));

            assertTrue(board.getCountBlackCells() > board.getCountWhiteCells());
        } catch (final ServerException e) {
            e.printStackTrace();
        }
    }

}