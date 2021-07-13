package models.board;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import models.base.Cell;

import java.util.*;

/**
 * Стандартная игровая доска. с двумя черными и двумя белыми фишками в середине.
 */
@Data
@Slf4j
@EqualsAndHashCode
public class Board {
    private final Map<Cell, String> tiles;
    public static final int BOARD_SIZE = 8;
    private final Map<Point, Cell> cells;
    private int countBlackCells = 0;
    private int countWhiteCells = 0;
    private int countEmpty;


    public Board() {
        countEmpty = BOARD_SIZE * BOARD_SIZE;
        cells = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cells.put(new Point(i, j), Cell.EMPTY);
            }
        }
        tiles = new HashMap<>();
        init();
    }

    private void init() {
        tiles.put(Cell.EMPTY, "_");
        tiles.put(Cell.BLACK, "#");
        tiles.put(Cell.WHITE, "O");
        cells.put(new Point(3, 3), Cell.WHITE);
        cells.put(new Point(3, 4), Cell.BLACK);
        cells.put(new Point(4, 3), Cell.BLACK);
        cells.put(new Point(4, 4), Cell.WHITE);
        countBlackCells = 2;
        countWhiteCells = 2;
        countEmpty = 60;
    }

    /**
     * Возвращает одно из трех состояний клетки игровой доски:
     * BLACK, WHITE, EMPTY.
     * @throws GameException
     */
    public Cell getCell(final int x, final int y) throws GameException {
        return getCell(new Point(x, y));
    }

    /**
     * Возвращает одно из трех состояний клетки игровой доски:
     * BLACK, WHITE, EMPTY.
     * @throws GameException
     */
    public Cell getCell(final Point point) throws GameException {
        checkPoint(point);
        return cells.get(point);
    }

    /**
     * Меняет состояние клетки доски.
     *
     * @param cell новое состояние
     * @throws GameException
     */
    public void setCell(final int x, final int y, Cell cell) throws GameException {
        setCell(new Point(x, y), cell);
    }

    /**
     * Меняет состояние клетки доски.
     *
     * @param cell новое состояние
     * @throws GameException
     */
    public void setCell(final Point point, final Cell cell) throws GameException {
        checkPoint(point);
        checkCell(cell);
        Cell before = getCell(point);
        switch (before) {
            case EMPTY: {
                countEmpty--;
                break;
            }
            case BLACK: {
                countBlackCells--;
                break;
            }
            case WHITE: {
                countWhiteCells--;
                break;
            }
            default:
                throw new GameException(GameErrorCode.INVALID_CELL);
        }
        switch (cell) {
            case EMPTY: {
                countEmpty++;
                break;
            }
            case BLACK: {
                countBlackCells++;
                break;
            }
            case WHITE: {
                countWhiteCells++;
                break;
            }
            default:
                throw new GameException(GameErrorCode.INVALID_CELL);
        }
        cells.put(point, cell);
    }


    /**
     * Меняет фишку доски в позиции (x, y) на противоположную.
     * Если в клетке была пустая фишка, то выбрасывает GameException.
     *
     * @param x координата по горизонтали
     * @param y координата по вуертикали (сверху вниз)
     * @throws GameException
     */
    public void reverseCell(final int x, final int y) throws GameException {
        reverseCell(new Point(x, y));
    }

    /**
     * Меняет фишку доски в позиции point на противоположную.
     * Если в клетке была пустая фишка, то выбрасывает GameException.
     * @param point позиция
     * @throws GameException
     */
    public void reverseCell(final Point point) throws GameException {
        Cell cell = getCell(point);
        if (cell == Cell.EMPTY) {
            log.error("Bad reverseCell {}, {}", point, cell, new GameException(GameErrorCode.CELL_IS_EMPTY));
            throw new GameException(GameErrorCode.CELL_IS_EMPTY);
        }
        if (cell == Cell.WHITE) {
            setCell(point, Cell.BLACK);
        } else {
            setCell(point, Cell.WHITE);
        }
    }

    /**
     * Переворачивает все фишки, переданные на вход функции.
     * @param points массив позиций доски для переворота.
     * @throws GameException
     */
    public void reverseCellAll(final Collection<Point> points) throws GameException {
        if (points == null) {
            throw new GameException(GameErrorCode.POINTS_NOT_FOUND);
        }
        for (Point p : points) {
            reverseCell(p);
        }
    }

    //TODO: move func to Point
    public void checkPoint(final Point point) throws GameException {
        if (!validate(point)) {
            log.error("Bad checkPoint {}", point, new GameException(GameErrorCode.BAD_POINT));
            throw new GameException(GameErrorCode.BAD_POINT);
        }
    }

    public void checkCell(final Cell cell) throws GameException {
        if (cell == null) {
            log.error("Bad checkCell", new GameException(GameErrorCode.INVALID_CELL));
            throw new GameException(GameErrorCode.INVALID_CELL);
        }
    }

    /**
     * @return true, если координаты точки находятся в пределах игровой доски.
     */
    public boolean validate(final Point point) {
        return point != null
                && point.getX() >= 0
                && point.getY() >= 0
                && point.getX() < BOARD_SIZE
                && point.getY() < BOARD_SIZE;
    }

    /**
     * @return  Возвращает представление игровой доски в виде строки.
     * @throws GameException
     */
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
        return "Board{" +
                "cells=" + cells +
                ", countBlackCells=" + countBlackCells +
                ", countWhiteCells=" + countWhiteCells +
                ", countEmpty=" + countEmpty +
                '}';
    }
}
