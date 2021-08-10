package models.base.interfaces;

import exception.ServerException;
import models.base.Cell;
import models.board.Point;

import java.util.Collection;

public interface GameBoard {
    int getSize();

    Cell getCell(final int x, final int y) throws ServerException;

    Cell getCell(final Point point) throws ServerException;

    void setCell(final int x, final int y, Cell cell) throws ServerException;

    void setCell(final Point point, final Cell cell) throws ServerException;

    void reverseCell(final int x, final int y) throws ServerException;

    void reverseCell(final Point point) throws ServerException;

    void reverseCells(final Collection<Point> points) throws ServerException;

    int getCountBlackCells();

    int getCountWhiteCells();

    int getCountEmpty();

    boolean validate(final Point point);

    GameBoard clone();

    String toString();
}
