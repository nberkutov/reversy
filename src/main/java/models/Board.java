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
    private Map<Cell, String> tiles;
    public static final int BOARD_SIZE = 8;
    private final Map<Point, Cell> cells;
    private int countBlack = 0;
    private int countWhite = 0;
    private int countEmpty = 0;

    public Board() {
        tiles = new HashMap<>();
        tiles.put(Cell.EMPTY, "□");
        tiles.put(Cell.BLACK, "●");
        tiles.put(Cell.WHITE, "○");
        cells = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cells.put(new Point(i, j), Cell.EMPTY);
            }
        }
        cells.put(new Point(3, 3), Cell.WHITE);
        cells.put(new Point(3, 4), Cell.BLACK);
        cells.put(new Point(4, 3), Cell.BLACK);
        cells.put(new Point(4, 4), Cell.WHITE);
    }

    public Board(Map<Point, Cell> mapCell) {
        this.cells = mapCell;
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
        checkPoint(point);
        Cell cell = cells.get(point);
        if (cell.equals(Cell.EMPTY)) {
            log.error("Bad reverseCell {}, its cell is {}", point, cell);
            throw new GameException(GameErrorCode.CELL_IS_EMPTY);
        }

        if (cell.equals(Cell.WHITE)) {
            cells.put(point, Cell.BLACK);
        } else {
            cells.put(point, Cell.WHITE);
        }
    }

    private void checkPoint(Point point) throws GameException {
        if (!validation(point)) {
            log.error("Bad checkPoint {}", point);
            throw new GameException(GameErrorCode.BAD_POINT);
        }
    }

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
