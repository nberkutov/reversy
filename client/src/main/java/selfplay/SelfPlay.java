package selfplay;

import exception.ServerException;
import gui.GameGUI;
import gui.WindowGUI;
import models.ArrayBoard;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import org.apache.commons.lang3.time.StopWatch;
import player.Player;
import strategy.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelfPlay {
    private static final int GAMES_COUNT = 10;
    private static final long DELAY = 0L;

    public static void main(final String[] args) {
        int blackWins = 0;
        int whiteWins = 0;

        final Player player1 = new BotPlayer("Minimax1", new ExpectimaxStrategy(3, Utility::multiHeuristic));
        final Player player2 = new BotPlayer("Minimax2", new RandomStrategy());

        final GameBoard board = new ArrayBoard();

        player1.setColor(PlayerColor.BLACK);
        player2.setColor(PlayerColor.WHITE);
        final GameGUI gui = new WindowGUI();

        final List<Long> moveTime = new ArrayList<>();
        for (int i = 0; i < GAMES_COUNT; i++) {
            System.out.printf("Game %d started\n", i);
            final Game game = new Game(player1, player2, new ArrayBoard());
            GameBoard gameBoard = board;
            final StopWatch stopWatch = new StopWatch();
            try {
                while (game.getGameState() != GameState.END) {
                    if (game.getGameState() == GameState.BLACK_MOVE) {
                        stopWatch.start();
                    }
                    gameBoard = game.playNext();
                    if (game.getGameState() == GameState.BLACK_MOVE) {
                        stopWatch.stop();
                        moveTime.add(stopWatch.getTime(TimeUnit.MILLISECONDS));
                        stopWatch.reset();
                    }
                    gui.updateGUI(gameBoard, game.getGameState(), "RANDOM");
                }
                if (gameBoard.getCountBlackCells() > gameBoard.getCountWhiteCells()) {
                    blackWins++;
                } else {
                    whiteWins++;
                }
            } catch (final ServerException e) {
                e.printStackTrace();
            }
        }

        long timeSum = 0L;
        for (final long t : moveTime) {
            timeSum += t;
        }
        double avgTime = 0;
        if (!moveTime.isEmpty()) {
            avgTime = timeSum / (double) moveTime.size();
        }
        System.out.println("Avg move time: " + avgTime);
        System.out.printf("BLACK %s - %d\n", player1.getNickname(), blackWins);
        System.out.printf("WHITE %s - %d\n", player2.getNickname(), whiteWins);
    }
}
