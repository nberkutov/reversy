package strategy;

import base.Strategy;
import exception.ServerException;
import logic.BoardLogic;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements Strategy {
    private PlayerColor color;

    @Override
    public Point move(final GameBoard board) throws ServerException {
        final List<Point> points = BoardLogic.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }

    @Override
    public void setColor(final PlayerColor color) {
        this.color = color;
    }
}
