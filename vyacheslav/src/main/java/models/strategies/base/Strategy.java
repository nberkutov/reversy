package models.strategies.base;

import exception.ServerException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;


public interface Strategy {
    Point getMove(GameBoard board, PlayerColor color) throws ServerException;
}
