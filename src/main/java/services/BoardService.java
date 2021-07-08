package services;

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
public class BoardService {
    private final Board board;

    public BoardService(Board board) {
        this.board = board;
    }

    public void makeMove(Point point, PlayerColor color) throws GameException {
        checkPlayerColor(color);
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
        board.reverseCellAll(pointsForReverse);
        board.setCell(point, cell);
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
        if (player == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
        return !getAvailableMoves(player.getColor()).isEmpty();
    }

    public List<Point> getAvailableMoves(PlayerColor color) throws GameException {
        checkPlayerColor(color);
        return getAvailableMoves(Cell.valueOf(color));
    }

    public List<Point> getAvailableMoves(Cell cell) throws GameException {
        board.checkCell(cell);
        checkCellOnEmpty(cell);
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
        board.checkPoint(point);
        checkCellOnEmpty(cell);
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

    private void checkCellOnEmpty(Cell cell) throws GameException {
        board.checkCell(cell);
        if (isCellEmpty(cell)) {
            log.error("Bad checkCellOnEmpty", new GameException(GameErrorCode.INVALID_CELL));
            throw new GameException(GameErrorCode.INVALID_CELL);
        }
    }

    private void checkPlayerColor(PlayerColor color) throws GameException {
        if (color == null) {
            log.error("Bad checkPlayerColor", new GameException(GameErrorCode.INVALID_PLAYER_COLOR));
            throw new GameException(GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

    private boolean isCellEmpty(Point point) throws GameException {
        return isCellEmpty(board.getCell(point));
    }

    private boolean isCellEmpty(Cell cell) {
        return cell.equals(Cell.EMPTY);
    }
}
