package org.example.models.strategies;

import lombok.AllArgsConstructor;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.ai.traversal.HeaderThread;
import org.example.models.ai.traversal.TraversalEnum;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Strategy;


@AllArgsConstructor
public class TraversalStrategy implements Strategy {
    private final TraversalEnum option;
    private final int time;

    public TraversalStrategy(final TraversalEnum option) {
        this(option, 3);
    }

    @Override
    public Point getMove(final GameBoard board, final PlayerColor color) throws ServerException {
        try {
            final HeaderThread header = new HeaderThread(board, color, option);
            header.start();
            header.join();
            return header.getEndResult().getState().getMove();
        } catch (final InterruptedException e) {
            throw new ServerException(GameErrorCode.AI_ERROR);
        }
    }
}
