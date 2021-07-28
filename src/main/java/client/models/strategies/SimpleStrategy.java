package client.models.strategies;

import exception.GameException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

public class SimpleStrategy extends Strategy {

    @Override
    public boolean additionTriggerEvaluationCall(final GameBoard board, final PlayerColor color, final Point point, final int depth, final int maxDepth) {
        return false;
    }

    @Override
    public int funcEvaluation(final GameBoard board, final PlayerColor color, final Point point, final int depth, final int maxDepth) throws GameException {
        return BoardService.getCountCellByPlayerColor(board, color);
    }

}
