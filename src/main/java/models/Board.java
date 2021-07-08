package models;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.base.Cell;

import java.util.*;

@Data
@Slf4j
public class Board {
    public static final int BOARD_SIZE = 8;
    private Map<Cell, String> tiles;
    private final Map<Point, Cell> cells;
    private int countBlack = 0;
    private int countWhite = 0;
    private int countEmpty;

    public Board() throws GameException {
        cells = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cells.put(new Point(i, j), Cell.EMPTY);
            }
        }
        setCell(new Point(3, 3), Cell.WHITE);
        setCell(new Point(3, 4), Cell.BLACK);
        setCell(new Point(4, 3), Cell.BLACK);
        setCell(new Point(4, 4), Cell.WHITE);
        countBlack = 2;
        countWhite = 2;
        countEmpty = BOARD_SIZE * BOARD_SIZE - 4;
    }

    public Board(Map<Point, Cell> mapCell) {
        this.cells = mapCell;
    }


    public Cell getCell(int x, int y) throws GameException {
        return getCell(new Point(x, y));
    }

    public Cell getCell(Point point) throws GameException {
        validatePoint(point);
        return cells.get(point);
    }

    public void setCell(int x, int y, Cell cell) throws GameException {
        setCell(new Point(x, y), cell);
    }

    public void setCell(Point point, Cell cell) throws GameException {
        validatePoint(point);
        Cell before = getCell(point);
        switch (before) {
            case EMPTY: {
                countEmpty--;
                break;
            }
            case BLACK: {
                countBlack--;
                break;
            }
            case WHITE: {
                countWhite--;
                break;
            }
        }
        switch (cell) {
            case EMPTY: {
                countEmpty++;
                break;
            }
            case BLACK: {
                countBlack++;
                break;
            }
            case WHITE: {
                countWhite++;
                break;
            }
        }
        cells.put(point, cell);
    }

    public long getCountCell(Cell cell) {
        return cells.values().stream().filter(x -> x.equals(cell)).count();
    }

    private void validatePoint(Point point) throws GameException {
        if (!isValid(point)) {
            log.error("Bad checkPoint {}", point, new GameException(GameErrorCode.BAD_POINT));
            throw new GameException(GameErrorCode.BAD_POINT);
        }
    }

    public boolean isValid(Point point) {
        return point != null
                && point.getX() >= 0
                && point.getY() >= 0
                && point.getX() < BOARD_SIZE
                && point.getY() < BOARD_SIZE;
    }

    @Override
    public String toString() {
        return "Board{}";
    }

    public Board copy() throws GameException {
        Board newBoard = new Board();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                newBoard.setCell(j, i, getCell(j, i));
            }
        }
        return newBoard;
    }
}
