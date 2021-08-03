package services;

import client.models.MinimaxBotPlayer;
import client.models.Player;
import client.models.RandomBotPlayer;
import exception.GameException;
import gui.GameGUI;
import gui.NoGUI;
import gui.WindowGUI;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.game.MyGame;

public class MySelfPlay {
    private static final int GAMES_COUNT = 20;

    public static void main(String[] args) {
        int blackWins = 0;
        int whiteWins = 0;

        Player player1 = new MinimaxBotPlayer("minimax");
        Player player2 = new RandomBotPlayer("random");
        GameBoard board = new Board();

        player1.setColor(PlayerColor.BLACK);
        player2.setColor(PlayerColor.WHITE);
        GameGUI gui = new WindowGUI();

        for (int i = 0; i < GAMES_COUNT; i++) {
            System.out.printf("Game %d started\n", i);
            MyGame game = new MyGame(player1, player2, new Board());
            GameBoard gameBoard = board;
            try {
                while (game.getGameState() != GameState.END) {
                    //Thread.sleep(10);
                    gameBoard = game.playNext();
                    gui.updateGUI(gameBoard, game.getGameState());
                }
                //gui.updateGUI(board, game.getGameState());
                if (gameBoard.getCountBlackCells() > gameBoard.getCountWhiteCells()) {
                    blackWins++;
                    System.out.println("Minimax wins");
                } else {
                    whiteWins++;
                }
            } catch (GameException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("BLACK %s - %d\n", player1.getNickname(), blackWins);
        System.out.printf("WHITE %s - %d\n", player2.getNickname(), whiteWins);
    }
}
