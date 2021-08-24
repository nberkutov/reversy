package selfplay;

import exception.ServerException;
import gui.GameGUI;
import gui.WindowGUI;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import player.Player;
import strategy.*;

public class MySelfPlay {
    private static final int GAMES_COUNT = 10;
    private static final long DELAY = 0L;
    //private static final Logger logger = Logger.getLogger(MySelfPlay.class);

    public static void main(final String[] args) {
        int blackWins = 0;
        int whiteWins = 0;

        final Player player1 = new BotPlayer("Minimax1", new MinimaxStrategy(3, Utility::multiHeuristic));
        final Player player2 = new BotPlayer("Random2", new MinimaxStrategy(3, Utility::advanced));

        //final Player player2 = new RandomBotPlayer("randomBot");
        final GameBoard board = new ArrayBoard();

        player1.setColor(PlayerColor.BLACK);
        player2.setColor(PlayerColor.WHITE);
        final GameGUI gui = new WindowGUI();

        for (int i = 0; i < GAMES_COUNT; i++) {
            System.out.printf("Game %d started\n", i);
            final MyGame game = new MyGame(player1, player2, new ArrayBoard());
            GameBoard gameBoard = board;
            try {
                while (game.getGameState() != GameState.END) {
                    // Thread.sleep(DELAY);
                    gameBoard = game.playNext();
                    gui.updateGUI(gameBoard, game.getGameState(), "RANDOM");
                }
                if (gameBoard.getCountBlackCells() > gameBoard.getCountWhiteCells()) {
                    blackWins++;
                } else {
                    whiteWins++;
                }
                //logger.info(String.format("black: %d white: %d", blackWins, whiteWins));
            } catch (final ServerException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("BLACK %s - %d\n", player1.getNickname(), blackWins);
        System.out.printf("WHITE %s - %d\n", player2.getNickname(), whiteWins);
    }
}
