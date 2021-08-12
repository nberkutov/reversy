package models.strategies.base;

import exception.ServerException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;


public interface Algorithm {
    boolean triggerEvaluationCall(GameBoard board, PlayerColor color, Point point) throws ServerException;

    int funcEvaluation(GameBoard board, PlayerColor color, Point point) throws ServerException;
}
