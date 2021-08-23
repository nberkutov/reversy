package org.example.logic;

import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.ArrayBoard;
import org.example.models.board.Board;

import static org.example.models.GameProperties.boardDefault;

public class BoardFactory {
    private BoardFactory() {
    }

    public static GameBoard generateStartedBoard() throws ServerException {
        final GameBoard board;
        switch (boardDefault) {
            case ARRAY_BOARD:
                board = new ArrayBoard();
                break;

            case MAP_BOARD:
                board = new Board();
                break;
            default:
                throw new ServerException(GameErrorCode.BOARD_NOT_FOUND);
        }
        board.updateTextCells();
        return board;
    }
}
