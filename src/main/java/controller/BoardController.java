package controller;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.Board;
import models.base.Cell;
import models.Point;
import models.base.PlayerColor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static models.Board.BOARD_SIZE;

@Data
@Slf4j
public class BoardController {
    private final Board board;

    public BoardController(Board board) {
        this.board = board;
    }

    public void makeMove(Point point, Cell cell) throws GameException {
        moveAndReverse(point, cell);
    }

    public void makeMove(Point point, PlayerColor color) throws GameException {
        makeMove(point, Cell.valueOf(color));
    }

    public int getCountWhite() {
        return board.getCountWhite();
    }

    public int getCountBlack() {
        return board.getCountWhite();
    }

    public int getCountEmpty() {
        return board.getCountEmpty();
    }

    private void moveAndReverse(Point point, Cell cell) throws GameException {
        List<Point> moves = getCellInAllDirection(point, cell);

        if (moves.isEmpty()) {
            throw new GameException(GameErrorCode.INVALID_MOVE);
        }

        Set<Point> pointsForReverse = new HashSet<>();
        for (Point target : moves) {
            pointsForReverse.addAll(getPointsForReverse(point, target));
        }
        board.reverseCellAll(pointsForReverse);
    }

    private Set<Point> getPointsForReverse(Point point, Point target) {
        Set<Point> points = new HashSet<>();
        Point p = new Point(point.getX(), point.getY());
        while (!p.equals(target)) {
            if (p.getX() - target.getX() < 0) {
                p.setX(p.getX() + 1);
            } else {
                p.setX(p.getX() - 1);
            }
            if (p.getY() - target.getY() < 0) {
                p.setY(p.getX() + 1);
            } else {
                p.setY(p.getX() - 1);
            }
            points.add(p);
        }
        points.remove(target);
        return points;
    }

    public List<Point> getAvailableMoves(PlayerColor color) throws GameException {
        return getAvailableMoves(Cell.valueOf(color));
    }

    public List<Point> getAvailableMoves(Cell cell) throws GameException {
        Set<Point> points = new HashSet<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Point checkPoint = new Point(i, j);
                if (isCellEmpty(checkPoint) && !getCellInAllDirection(checkPoint, cell).isEmpty()) {
                    points.add(checkPoint);
                }
            }
        }
        return new ArrayList<>(points);
    }

    public List<Point> getCellInAllDirection(Point point, Cell cell) throws GameException {
        Set<Point> points = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                Point checkPoint = new Point(point.getX() + i, point.getY() + j);
                if (board.validation(checkPoint)
                        && !isCellEmpty(checkPoint)
                        && !board.getCell(checkPoint).equals(cell)) {
                    Point found = getPointInDirection(checkPoint, cell, i, j);
                    if (found != null) {
                        points.add(found);
                    }
                }
            }
        }
        return new ArrayList<>(points);
    }

    private Point getPointInDirection(Point point, Cell cell, int difX, int difY) throws GameException {
        Point p = new Point(point.getX(), point.getY());
        do {
            p.setX(p.getX() + difX);
            p.setY(p.getY() + difY);
            if (!board.validation(p) || isCellEmpty(p)) {
                return null;
            }
        } while (!board.getCell(p).equals(cell));
        return p;
    }

    private boolean isCellEmpty(Point point) throws GameException {
        return board.getCell(point).equals(Cell.EMPTY);
    }
}
