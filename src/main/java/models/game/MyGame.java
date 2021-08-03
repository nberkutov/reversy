package models.game;

import client.models.Player;
import exception.GameException;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;

public class MyGame {
    private final Player blackPlayer;
    private final Player whitePlayer;
    private final GameBoard board;
    private GameState gameState;

    public MyGame(Player blackPlayer, Player whitePlayer, GameBoard board) {
        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;
        this.board = board;
        gameState = GameState.BLACK_MOVE;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameBoard playNext() throws GameException {
        List<Point> blackMoves = BoardService.getAvailableMoves(board, blackPlayer.getColor());
        List<Point> whiteMoves = BoardService.getAvailableMoves(board, whitePlayer.getColor());
        if (board.getCountEmpty() == 0 || blackMoves.isEmpty() && whiteMoves.isEmpty()) {
            gameState = GameState.END;
            return board;
        }
        if (gameState == GameState.BLACK_MOVE) {
            gameState = GameState.WHITE_MOVE;
            if (blackMoves.isEmpty()) {
                return board;
            }
            Point move = blackPlayer.move(board);
            BoardService.makeMove(board, move, Cell.valueOf(blackPlayer.getColor()));

        } else if (gameState == GameState.WHITE_MOVE) {
            gameState = GameState.BLACK_MOVE;
            if (whiteMoves.isEmpty()) {
                return board;
            }
            Point move = whitePlayer.move(board);
            BoardService.makeMove(board, move, Cell.valueOf(whitePlayer.getColor()));
        }
        return board;
    }
}
