package models.base;

import exception.GameException;
import models.board.Point;

public interface GamePlayer {
    Point move(GameBoard board) throws GameException;
}
