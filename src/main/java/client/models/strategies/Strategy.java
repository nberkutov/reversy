package client.models.strategies;

import exception.GameException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

public abstract class Strategy {
    public abstract boolean triggerEvaluationCall(final GameBoard board, final PlayerColor color, final Point point, final int depth, final int maxDepth) throws GameException;

    public abstract int funcEvaluation(final GameBoard board, final PlayerColor color, final Point point, final int depth, final int maxDepth) throws GameException;
}
