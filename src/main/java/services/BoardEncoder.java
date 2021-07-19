package services;

import exception.GameException;
import models.base.GameBoard;

public class BoardEncoder {
    public static String toString(GameBoard board) throws GameException {
        StringBuilder boardBuilder = new StringBuilder();
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                char c = '0';
                switch (board.getCell(x, y)) {
                    case EMPTY:
                        break;
                    case WHITE:
                        c = 'w';
                        break;
                    case BLACK:
                        c = 'b';
                        break;
                }
                boardBuilder.append(c);
            }
        }
        return boardBuilder.toString();
    }
}
