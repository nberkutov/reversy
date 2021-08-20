package selfplay;


import exception.ServerException;
import logic.BoardLogic;
import player.Player;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.List;

public class MyGame {
    private final Player blackPlayer;
    private final Player whitePlayer;
    private final GameBoard board;
    private GameState gameState;

    public MyGame(final Player blackPlayer, final Player whitePlayer, final GameBoard board) {
        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;
        this.board = board;
        gameState = GameState.BLACK_MOVE;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameBoard playNext() throws ServerException {
        final List<Point> blackMoves = BoardLogic.getAvailableMoves(board, blackPlayer.getColor());
        final List<Point> whiteMoves = BoardLogic.getAvailableMoves(board, whitePlayer.getColor());
        if (board.getCountEmpty() == 0 || blackMoves.isEmpty() && whiteMoves.isEmpty()) {
            gameState = GameState.END;
            return board;
        }
        if (gameState == GameState.BLACK_MOVE) {
            gameState = GameState.WHITE_MOVE;
            if (blackMoves.isEmpty()) {
                return board;
            }
            final Point move = blackPlayer.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(blackPlayer.getColor()));

        } else if (gameState == GameState.WHITE_MOVE) {
            gameState = GameState.BLACK_MOVE;
            if (whiteMoves.isEmpty()) {
                return board;
            }
            final Point move = whitePlayer.move(board);
            BoardLogic.makeMove(board, move, Cell.valueOf(whitePlayer.getColor()));
        }
        return board;
    }
}
