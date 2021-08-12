package logic;

import exception.GameErrorCode;
import exception.ServerException;
import models.base.interfaces.GameBoard;
import models.board.ArrayBoard;
import models.board.Board;
import models.board.BoardDefault;

public class BoardFactory {
    private static BoardDefault boardDefault = BoardDefault.ARRAY_BOARD;

    private BoardFactory() {
    }

    public static GameBoard generateStartedBoard() throws ServerException {
        switch (boardDefault) {
            case ARRAY_BOARD:
                return new ArrayBoard();

            case MAP_BOARD:
                return new Board();
        }
        throw new ServerException(GameErrorCode.BOARD_NOT_FOUND);
    }
}
