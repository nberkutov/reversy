package models.strategies;


import exception.ServerException;
import logic.BoardLogic;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements Strategy {
    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        List<Point> points = BoardLogic.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}
