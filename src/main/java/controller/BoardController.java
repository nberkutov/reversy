package controller;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.Board;
import models.Player;
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
        return board.getCountBlack();
    }

    public int getCountEmpty() {
        return board.getCountEmpty();
    }

    public boolean isPossibleMove(Player player) throws GameException {
        return !getAvailableMoves(player.getColor()).isEmpty();
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
        board.setCell(point, cell);
    }

    private Set<Point> getPointsForReverse(Point point, Point target) {
        Set<Point> points = new HashSet<>();
        Point p = new Point(point.getX(), point.getY());
        while (!p.equals(target)) {
            if (p.getX() < target.getX()) {
                p.setX(p.getX() + 1);
            } else if (p.getX() > target.getX()) {
                p.setX(p.getX() - 1);
            }
            if (p.getY() < target.getY()) {
                p.setY(p.getY() + 1);
            } else if (p.getY() > target.getY()) {
                p.setY(p.getY() - 1);
            }
            points.add(new Point(p.getX(), p.getY()));
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
        boolean wasOtherCell = false;
        //left
        for (int x = point.getX() - 1; x >= 0; x--) {
            Point checkPoint = new Point(x, point.getY());

            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }
        wasOtherCell = false;
        //left+up
        for (int x = point.getX() - 1, y = point.getY() - 1; x >= 0 && y >= 0; x--, y--) {
            Point checkPoint = new Point(x, y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }
        wasOtherCell = false;
        //up
        for (int y = point.getY() - 1; y >= 0; y--) {
            Point checkPoint = new Point(point.getX(), y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }
        wasOtherCell = false;
        //right+up
        for (int x = point.getX() + 1, y = point.getY() - 1; x < BOARD_SIZE && y >= 0; x++, y--) {
            Point checkPoint = new Point(x, y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }
        wasOtherCell = false;
        //right
        for (int x = point.getX() + 1; x < BOARD_SIZE; x++) {
            Point checkPoint = new Point(x, point.getY());
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }
        wasOtherCell = false;
        //right+down
        for (int x = point.getX() + 1, y = point.getY() + 1; x < BOARD_SIZE && y < BOARD_SIZE; x++, y++) {
            Point checkPoint = new Point(x, y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }
        wasOtherCell = false;
        //down
        for (int y = point.getY() + 1; y < BOARD_SIZE; y++) {
            Point checkPoint = new Point(point.getX(), y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }
        wasOtherCell = false;
        //left+down
        for (int x = point.getX() - 1, y = point.getY() + 1; x >= 0 && y < BOARD_SIZE; x--, y++) {
            Point checkPoint = new Point(x, y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                if (wasOtherCell) {
                    points.add(checkPoint);
                }
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                wasOtherCell = true;
            }
        }

        return new ArrayList<>(points);
    }

    private boolean isCellEmpty(Point point) throws GameException {
        return board.getCell(point).equals(Cell.EMPTY);
    }
}
