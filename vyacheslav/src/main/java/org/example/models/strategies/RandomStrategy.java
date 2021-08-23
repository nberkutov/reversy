package org.example.models.strategies;


import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Strategy;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements Strategy {
    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        List<Point> points = BoardLogic.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}
