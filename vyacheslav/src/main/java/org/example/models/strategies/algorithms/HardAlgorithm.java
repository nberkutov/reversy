package org.example.models.strategies.algorithms;


import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.GameProperties;
import org.example.models.base.Cell;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Algorithm;

import java.util.Arrays;
import java.util.List;

public class HardAlgorithm implements Algorithm {
    @Override
    public boolean triggerEvaluationCall(final GameBoard board, final PlayerColor color, final Point point) throws ServerException {
        return false;
    }

    @Override
    public int funcEvaluation(final GameBoard board, final PlayerColor color, final Point point) throws ServerException {
        if (countCorners(board, color) == 0) {
            return 0;
        }
        return BoardLogic.getCountCellByPlayerColor(board, color) + countCorners(board, color) * 50;
    }

    private int countCorners(final GameBoard board, final PlayerColor color) throws ServerException {
        int count = 0;
        for (final Point p : cornerPoints()) {
            if (board.getCell(p) == Cell.valueOf(color)) {
                count++;
            }
        }
        return count;
    }

    private List<Point> cornerPoints() {
        return Arrays.asList(
                new Point(0, 0),
                new Point(0, GameProperties.BOARD_SIZE - 1),
                new Point(GameProperties.BOARD_SIZE - 1, 0),
                new Point(GameProperties.BOARD_SIZE - 1, GameProperties.BOARD_SIZE - 1));
    }
}
