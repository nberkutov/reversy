package org.example.models.strategies.algorithms;


import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Algorithm;

public class SimpleAlgorithm implements Algorithm {
    @Override
    public boolean triggerEvaluationCall(final GameBoard board, final PlayerColor color, final Point point) throws ServerException {
        return false;
    }

    @Override
    public int funcEvaluation(final GameBoard board, final PlayerColor color, final Point point) throws ServerException {
        return BoardLogic.getCountCellByPlayerColor(board, color);
    }
}
