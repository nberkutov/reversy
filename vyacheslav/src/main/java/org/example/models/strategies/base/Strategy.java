package org.example.models.strategies.base;

import org.example.exception.ServerException;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;


public interface Strategy {
    Point getMove(GameBoard board, PlayerColor color) throws ServerException;
}
