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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StrangeAlgorithm implements Algorithm {
    private final Set<Point> movesForWin;
    private final Set<Point> movesForLose;
    private final Set<Point> movesAdditionalCheck;

    public StrangeAlgorithm() {
        movesForWin = new HashSet<>();
        movesForLose = new HashSet<>();
        movesAdditionalCheck = new HashSet<>();
        init();
    }

    private void init() {
        for (int i = 0; i < GameProperties.BOARD_SIZE; i++) {
            for (int j = 0; j < GameProperties.BOARD_SIZE; j++) {
                final Point p = new Point(i, j);
                if (isCornerPoint(p)) {
                    movesForWin.add(p);
                } else if (isNotSafeMove(p)) {
                    movesForLose.add(p);
                } else if (isMoveAdditionalCheck(p)) {
                    movesAdditionalCheck.add(p);
                }
            }
        }
    }

    private boolean isMoveAdditionalCheck(final Point p) {
        final Point corner = getNearestCornerPoint(p);
        if (corner == null) {
            return false;
        }
        int dif = Math.min(Math.abs(p.getX() - corner.getX()), Math.abs(p.getY() - corner.getY()));
        return dif == 0;
    }


    @Override
    public boolean triggerEvaluationCall(final GameBoard board, final PlayerColor color, final Point point) throws ServerException {
        return movesForWin.contains(point)
                || (movesForLose.contains(point) && !isSafeMove(board, point, color))
                ||
                (movesAdditionalCheck.contains(point)
                        && (!isPointBetweenEnemies(board, point, color)
                        || !isSafeMove(board, point, color)));
    }

    @Override
    public int funcEvaluation(final GameBoard board, final PlayerColor color, final Point point) throws ServerException {
        final int score = BoardLogic.getCountCellByPlayerColor(board, color);

        if (movesForLose.contains(point)) {
            if (isSafeMove(board, point, color)) {
                return score;
            }
            return 0;
        }

        if (movesAdditionalCheck.contains(point)) {
            if (isPointBetweenEnemies(board, point, color)) {
                return score;
            }
            if (isSafeMove(board, point, color)) {
                return score;
            }
            return 0;
        }

        if (isCornerPoint(point)) {
            return 100;
        }

        return score;
    }


    private boolean isPointBetweenEnemies(final GameBoard board, final Point point, final PlayerColor color) throws ServerException {
        final Cell enemy = Cell.valueOf(color.getOpponent());
        switch (findBorder(point)) {
            case 1:
            case 3:
                return isNearestEnemyInDirection(board, point, enemy, 0, -1)
                        && isNearestEnemyInDirection(board, point, enemy, 0, 1);
            case 2:
            case 4:
                return isNearestEnemyInDirection(board, point, enemy, -1, 0)
                        && isNearestEnemyInDirection(board, point, enemy, 1, 0);
            default:
                return false;
        }
    }

    private boolean isNearestEnemyInDirection(final GameBoard board, final Point point, final Cell cell, final int difX, final int difY) throws ServerException {
        final Point p = new Point(point.getX(), point.getY());
        do {
            p.setX(p.getX() + difX);
            p.setY(p.getY() + difY);
            if (!board.validate(p) || board.getCell(point) == Cell.EMPTY) {
                return false;
            }
        } while (!board.getCell(p).equals(cell));
        return true;
    }

    private int findBorder(final Point point) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (Math.abs(i) == Math.abs(j)) {
                    continue;
                }
                final Point outsideBorder = new Point(point.getX() + i, point.getY() + j);
                if (outsideBorder.getX() < 0) {
                    return 1;
                } else if (outsideBorder.getY() < 0) {
                    return 2;
                } else if (outsideBorder.getX() > GameProperties.BOARD_SIZE) {
                    return 3;
                } else if (outsideBorder.getY() > GameProperties.BOARD_SIZE) {
                    return 4;
                }
            }
        }
        return -1;
    }


    private boolean isCornerPoint(final Point point) {
        for (final Point check : cornerPoints()) {
            if (check.equals(point)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSafeMove(final GameBoard board, final Point point, final PlayerColor color) throws ServerException {
        final Point corner = getNearestCornerPointBoard(board, point, Cell.valueOf(color));
        if (corner == null) {
            return false;
        }
        final Cell need = Cell.valueOf(color);
        final int minX = Math.min(point.getX(), corner.getX());
        final int maxX = Math.max(point.getX(), corner.getX());
        final int minY = Math.min(point.getY(), corner.getY());
        final int maxY = Math.min(point.getY(), corner.getY());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (board.getCell(x, y) != need) {
                    return false;
                }
            }
        }
        return true;
    }

    private Point getNearestCornerPointBoard(final GameBoard board, final Point point, final Cell need) throws ServerException {
        double min = 100;
        Point result = null;
        for (final Point check : cornerPoints()) {
            if (board.getCell(check) != need) {
                continue;
            }
            final double dist = point.distance(check);
            if (min >= dist) {
                min = dist;
                result = check;
            }
        }
        return result;
    }

    private boolean isNotSafeMove(final Point point) {
        final Point corner = getNearestCornerPoint(point);
        if (corner == null) {
            return false;
        }
        return corner.distanceSq(point) <= 2;
    }

    private Point getNearestCornerPoint(final Point point) {
        double min = 100;
        Point result = null;
        for (final Point check : cornerPoints()) {
            double dist = point.distance(check);
            if (min >= dist) {
                min = dist;
                result = check;
            }
        }
        return result;
    }

    private List<Point> cornerPoints() {
        return Arrays.asList(
                new Point(0, 0),
                new Point(0, GameProperties.BOARD_SIZE - 1),
                new Point(GameProperties.BOARD_SIZE - 1, 0),
                new Point(GameProperties.BOARD_SIZE - 1, GameProperties.BOARD_SIZE - 1));
    }
}
