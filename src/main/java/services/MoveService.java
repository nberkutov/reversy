package services;

import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.Board;
import models.Player;
import models.base.Cell;
import models.Point;
import models.base.PlayerColor;

import java.util.*;

import static models.Board.BOARD_SIZE;

@Slf4j
public class MoveService {
    private final Board board;


    public MoveService(Board board) {
        this.board = board;
    }

    public Board getBoard() throws GameException {
        return board.copy();
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

    public void makeMove(Point point, PlayerColor color) throws GameException {
        makeMove(point, Cell.valueOf(color));
    }

    public void makeMove(Point point, Cell cell) throws GameException {
        List<Point> moves = getCellInAllDirection(point, cell);

        if (moves.isEmpty()) {
            throw new GameException(GameErrorCode.INVALID_MOVE);
        }

        Set<Point> pointsForReverse = new HashSet<>();
        for (Point target : moves) {
            pointsForReverse.addAll(getPointsForReverse(point, target));
        }
        reverseCells(pointsForReverse);
        board.setCell(point, cell);
    }

    public boolean isPossibleMove(Player player) throws GameException {
        return !getAvailableMoves(player.getColor()).isEmpty();
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

    public void reverseCells(Collection<Point> points) throws GameException {
        if (points == null) {
            throw new GameException(GameErrorCode.POINTS_NOT_FOUND);
        }
        for (Point p : points) {
            reverseCell(p);
        }
    }

    public void reverseCell(int x, int y) throws GameException {
        reverseCell(new Point(x, y));
    }

    public void reverseCell(final Point point) throws GameException {
        Cell cell = board.getCell(point);
        if (cell.equals(Cell.EMPTY)) {
            log.error("Bad reverseCell {}, {}", point, cell, new GameException(GameErrorCode.CELL_IS_EMPTY));
            throw new GameException(GameErrorCode.CELL_IS_EMPTY);
        }

        if (cell.equals(Cell.WHITE)) {
            board.setCell(point, Cell.BLACK);
        } else {
            board.setCell(point, Cell.WHITE);
        }
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
                if (board.isValid(checkPoint)
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
            if (!board.isValid(p) || isCellEmpty(p)) {
                return null;
            }
        } while (!board.getCell(p).equals(cell));
        return p;
    }

    private boolean isCellEmpty(Point point) throws GameException {
        return board.getCell(point).equals(Cell.EMPTY);
    }
}
