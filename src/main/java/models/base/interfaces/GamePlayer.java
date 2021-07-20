package models.base.interfaces;

import exception.GameException;
import models.board.Point;

public interface GamePlayer {
    Point move(final GameBoard board) throws GameException;
}
