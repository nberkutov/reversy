package strategy;

import base.Strategy;
import exception.ServerException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

public class EmptyStrategy implements Strategy {

    @Override
    public Point move(final GameBoard board) throws ServerException {
        return null;
    }

    @Override
    public void setColor(final PlayerColor color) {

    }
}
