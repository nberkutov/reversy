package models.base;

import exception.GameException;
import models.board.Point;

import java.util.Collection;

public interface GameBoard {
    int getSize();
    Cell getCell(final int x, final int y) throws GameException;

    Cell getCell(final Point point) throws GameException;

    void setCell(final int x, final int y, Cell cell) throws GameException;

    void setCell(final Point point, final Cell cell) throws GameException;

    void reverseCell(final int x, final int y) throws GameException;

    void reverseCell(final Point point) throws GameException;

    void reverseCells(final Collection<Point> points) throws GameException;

    int getCountBlackCells();

    int getCountWhiteCells();

    int getCountEmpty();

    boolean validate(final Point point);

    String toString();
}
