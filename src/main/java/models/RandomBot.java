package models;

import controller.BoardController;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.Cell;
import models.base.PlayerColor;

import java.util.*;

import static models.Board.BOARD_SIZE;

public class RandomBot extends Player {

    public RandomBot(long id, PlayerColor color, BoardController boardController) {
        super(id, color, boardController);
    }

    @Override
    public void nextMove() throws GameException {
        List<Point> points = getAvailableMoves(color);
        Point move = points.get(new Random().nextInt(points.size()));
        boardController.makeMove(move, color);
    }

    public List<Point> getAvailableMoves(PlayerColor color) throws GameException {
        return getAvailableMoves(Cell.valueOf(color));
    }

    public List<Point> getAvailableMoves(Cell cell) throws GameException {
        Set<Point> points = new HashSet<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Point checkPoint = new Point(i, j);
                if (!getCellInAllDirection(checkPoint, cell).isEmpty()) {
                    points.add(checkPoint);
                }
            }
        }
        return new ArrayList<>(points);
    }

    public List<Point> getCellInAllDirection(Point point, Cell cell) throws GameException {
        Set<Point> points = new HashSet<>();
        Board board = boardController.getBoard();
        boolean wasOtherCell = false;
        //left
        for (int x = point.getX() - 1; x >= 0; x--) {
            Point checkPoint = new Point(x, point.getY());

            if (isCellEmpty(board, checkPoint)) {
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
            if (isCellEmpty(board, checkPoint)) {
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
            if (isCellEmpty(board, checkPoint)) {
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
            if (isCellEmpty(board, checkPoint)) {
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
            if (isCellEmpty(board, checkPoint)) {
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
            if (isCellEmpty(board, checkPoint)) {
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
            if (isCellEmpty(board, checkPoint)) {
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
            if (isCellEmpty(board, checkPoint)) {
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

    private boolean isCellEmpty(Board board, Point point) throws GameException {
        return board.getCell(point).equals(Cell.EMPTY);
    }

}
