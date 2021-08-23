package org.example.models.base.interfaces;

import org.example.exception.ServerException;
import org.example.models.base.Cell;
import org.example.models.board.Point;

import java.util.Collection;

public interface GameBoard {
    int getSize();

    Cell getCell(final int x, final int y);

    Cell getCell(final Point point);

    void setCell(final int x, final int y, Cell cell);

    void setCell(final Point point, final Cell cell);

    void reverseCell(final int x, final int y) throws ServerException;

    void reverseCell(final Point point) throws ServerException;

    void reverseCells(final Collection<Point> points) throws ServerException;

    int getCountBlackCells();

    int getCountWhiteCells();

    int getCountEmpty();

    boolean validate(final Point point);

    GameBoard clone();

    String toString();

    void updateTextCells() throws ServerException;

    void updateCellsByText();

    String getTextCells();

    void setTextCells(final String string);
}
