package models;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import models.base.Cell;

import java.util.*;

@Data
@Slf4j
@EqualsAndHashCode
public class Board {
    private Map<Cell, String> tiles;
    public static final int BOARD_SIZE = 8;
    private final Map<Point, Cell> cells;
    private int countBlack = 0;
    private int countWhite = 0;
    private int countEmpty;

    public Board() throws GameException {
        countEmpty = (int) Math.pow(BOARD_SIZE, 2);
        tiles = new HashMap<>();
        tiles.put(Cell.EMPTY, "e");
        tiles.put(Cell.BLACK, "b");
        tiles.put(Cell.WHITE, "w");
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
    }

    public Cell getCell(int x, int y) throws GameException {
        return getCell(new Point(x, y));
    }

    public Cell getCell(Point point) throws GameException {
        checkPoint(point);
        return cells.get(point);
    }

    public void setCell(int x, int y, Cell cell) throws GameException {
        setCell(new Point(x, y), cell);
    }

    public void setCell(Point point, Cell cell) throws GameException {
        checkPoint(point);
        checkCell(cell);
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

    public void reverseCell(int x, int y) throws GameException {
        reverseCell(new Point(x, y));
    }

    public void reverseCellAll(Collection<Point> points) throws GameException {
        if (points == null) {
            throw new GameException(GameErrorCode.POINTS_NOT_FOUND);
        }
        for (Point p : points) {
            reverseCell(p);
        }
    }

    public void reverseCell(Point point) throws GameException {
        Cell cell = getCell(point);
        if (cell.equals(Cell.EMPTY)) {
            log.error("Bad reverseCell {}, {}", point, cell, new GameException(GameErrorCode.CELL_IS_EMPTY));
            throw new GameException(GameErrorCode.CELL_IS_EMPTY);
        }

        if (cell.equals(Cell.WHITE)) {
            setCell(point, Cell.BLACK);
        } else {
            setCell(point, Cell.WHITE);
        }
    }

    //TODO: move func to Point
    public void checkPoint(Point point) throws GameException {
        if (!validation(point)) {
            log.error("Bad checkPoint {}", point, new GameException(GameErrorCode.BAD_POINT));
            throw new GameException(GameErrorCode.BAD_POINT);
        }
    }

    public void checkCell(Cell cell) throws GameException {
        if (cell == null) {
            log.error("Bad checkCell", new GameException(GameErrorCode.INVALID_CELL));
            throw new GameException(GameErrorCode.INVALID_CELL);
        }
    }

    //TODO: rename to validate
    public boolean validation(Point point) {
        return point != null && point.getX() >= 0 && point.getY() >= 0 && point.getX() < BOARD_SIZE && point.getY() < BOARD_SIZE;
    }

    public String getVisualString() throws GameException {
        StringBuilder boardBuilder = new StringBuilder();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardBuilder.append(tiles.get(getCell(j, i))).append(" ");
            }
            boardBuilder.append("\n");
        }
        return boardBuilder.toString();
    }

    @Override
    public String toString() {
        return "Board{}";
    }

}
