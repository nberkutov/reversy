package models.strategies.algorithms;


import exception.ServerException;
import logic.BoardLogic;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Algorithm;

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
