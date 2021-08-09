package models.strategies;

import exception.GameErrorCode;
import exception.ServerException;
import lombok.AllArgsConstructor;
import models.ai.traversal.HeaderThread;
import models.ai.traversal.TraversalEnum;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;


@AllArgsConstructor
public class TraversalStrategy implements Strategy {
    private final TraversalEnum option;
    private final int time;

    public TraversalStrategy(TraversalEnum option) {
        this(option, 3);
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        try {
            HeaderThread header = new HeaderThread(board, color, option);
            header.start();
            header.join();
            return header.getEndResult().getState().getMove();
        } catch (InterruptedException e) {
            throw new ServerException(GameErrorCode.AI_ERROR);
        }
    }
}
