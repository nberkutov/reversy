package org.example.models.board;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.logic.BoardUtils;
import org.example.models.base.Cell;
import org.example.models.base.interfaces.GameBoard;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.example.models.GameProperties.BOARD_SIZE;

/**
 * Стандартная игровая доска. с двумя черными и двумя белыми фишками в середине.
 */
@Data
@EqualsAndHashCode
public class Board implements Serializable, GameBoard {
    private String textCells;
    private final Map<Point, Cell> cells;
    private final int size;
    private int countBlackCells = 0;
    private int countWhiteCells = 0;
    private int countEmpty;


    public Board() {
        size = BOARD_SIZE;
        countEmpty = BOARD_SIZE * BOARD_SIZE;
        cells = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cells.put(new Point(i, j), Cell.EMPTY);
            }
        }
        init();
    }

    private void init() {
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
     */
    public Cell getCell(final int x, final int y) {
        return getCell(new Point(x, y));
    }

    /**
     * Возвращает одно из трех состояний клетки игровой доски:
     * BLACK, WHITE, EMPTY.
     */
    public Cell getCell(final Point point) {
        validatePoint(point);
        return cells.get(point);
    }

    /**
     * Меняет состояние клетки доски.
     *
     * @param cell новое состояние
     */
    public void setCell(final int x, final int y, final Cell cell) {
        setCell(new Point(x, y), cell);
    }

    /**
     * Меняет состояние клетки доски.
     *
     * @param cell новое состояние
     */
    public void setCell(final Point point, final Cell cell) {
        validatePoint(point);
        checkCellIsNull(cell);
        final Cell before = getCell(point);
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
        }
        cells.put(point, cell);
    }


    /**
     * Меняет фишку доски в позиции (x, y) на противоположную.
     * Если в клетке была пустая фишка, то выбрасывает GameException.
     *
     * @param x координата по горизонтали
     * @param y координата по вуертикали (сверху вниз)
     * @throws ServerException
     */
    public void reverseCell(final int x, final int y) throws ServerException {
        reverseCell(new Point(x, y));
    }

    /**
     * Меняет фишку доски в позиции point на противоположную.
     * Если в клетке была пустая фишка, то выбрасывает GameException.
     *
     * @param point позиция
     * @throws ServerException
     */
    public void reverseCell(final Point point) throws ServerException {
        final Cell cell = getCell(point);
        if (cell == Cell.EMPTY) {
            throw new ServerException(GameErrorCode.INVALID_CELL);
        }
        if (cell == Cell.WHITE) {
            setCell(point, Cell.BLACK);
        } else {
            setCell(point, Cell.WHITE);
        }
    }

    /**
     * Переворачивает все фишки, переданные на вход функции.
     *
     * @param points массив позиций доски для переворота.
     * @throws ServerException
     */
    public void reverseCells(final Collection<Point> points) throws ServerException {
        if (points == null) {
            throw new ServerException(GameErrorCode.POINTS_NOT_FOUND);
        }
        for (final Point p : points) {
            reverseCell(p);
        }
    }

    @Override
    public boolean validate(final Point point) {
        return point != null &&
                point.getX() >= 0 && point.getX() < size
                && point.getY() >= 0 && point.getY() < size;
    }

    public void validatePoint(final Point point) {
        if (point == null) {
            throw new NullPointerException("Point is null");
        }
        if (!validate(point)) {
            throw new IllegalArgumentException(String.format("Illegal coordinates: (%d, %d) ", point.getX(), point.getY()));
        }
    }

    public void checkCellIsNull(final Cell cell) {
        if (cell == null) {
            throw new NullPointerException("Cell is null");
        }
    }

    @SneakyThrows
    @Override
    public Board clone() {
        final Board board = new Board();
        for (final Map.Entry<Point, Cell> entry : getCells().entrySet()) {
            board.setCell(entry.getKey(), entry.getValue());
        }
        return board;
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

    @Override
    public void updateTextCells() throws ServerException {
        setTextCells(BoardUtils.toString(this));
    }

    @Override
    public void updateCellsByText() {
        BoardUtils.updateCellsByTextCells(this);
    }
}
