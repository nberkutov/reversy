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

    private void moveAndReverse(Point point, Cell cell) throws GameException {
        Set<Point> forRevers = new HashSet<>();
        Set<Point> temp = new HashSet<>();
        //left
        for (int x = point.getX() - 1; x >= 0; x--) {
            Point checkPoint = new Point(x - 1, point.getY());
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }
        temp.clear();
        //left+up
        for (int x = point.getX() - 1, y = point.getY() - 1; x >= 0 && y >= 0; x--, y--) {
            Point checkPoint = new Point(x, y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }
        temp.clear();
        //up
        for (int y = point.getY() - 1; y >= 0; y--) {
            Point checkPoint = new Point(point.getX(), y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }
        temp.clear();
        //right+up
        for (int x = point.getX() + 1, y = point.getY() - 1; x < BOARD_SIZE && y >= 0; x++, y--) {
            Point checkPoint = new Point(x, y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }
        temp.clear();
        //right
        for (int x = point.getX() + 1; x < BOARD_SIZE; x++) {
            Point checkPoint = new Point(x, point.getY());
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }
        temp.clear();
        //right+down
        for (int x = point.getX() + 1, y = point.getY() + 1; x < BOARD_SIZE && y < BOARD_SIZE; x++, y++) {
            Point checkPoint = new Point(x, y);

            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }
        temp.clear();
        //down
        for (int y = point.getY() + 1; y < BOARD_SIZE; y++) {
            Point checkPoint = new Point(point.getX(), y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }
        temp.clear();
        //left+down
        for (int x = point.getX() - 1, y = point.getY() + 1; x >= 0 && y < BOARD_SIZE; x--, y++) {
            Point checkPoint = new Point(x, y);
            if (isCellEmpty(checkPoint)) {
                break;
            }
            if (board.getCell(checkPoint).equals(cell)) {
                forRevers.addAll(temp);
                break;
            }
            if (!board.getCell(checkPoint).equals(cell)) {
                temp.add(checkPoint);
            }
        }

        if (forRevers.isEmpty()) {
            throw new GameException(GameErrorCode.INVALID_MOVE);
        }

        for (Point p : forRevers) {
            board.reverseCell(p);
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
