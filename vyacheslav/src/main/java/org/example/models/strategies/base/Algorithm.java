package org.example.models.strategies.base;

import org.example.exception.ServerException;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;


public interface Algorithm {
    boolean triggerEvaluationCall(GameBoard board, PlayerColor color, Point point) throws ServerException;

    int funcEvaluation(GameBoard board, PlayerColor color, Point point) throws ServerException;
}
