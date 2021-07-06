package models;

import exception.GameException;
import models.base.PlayerColor;

import java.util.Set;

interface GameBoard {
    Cell getCell(int x, int y) throws GameException;
    Cell getCell(Point point) throws GameException;
    void setCell(int x, int y, Cell cell) throws GameException;
    void setCell(Point point, Cell cell) throws GameException;
    Set<Point> getCellInAllDirection(Point point, Cell cell) throws GameException;
    long getCountCell(Cell cell);
    Set<Point> getAvailableMoves(PlayerColor color);
    void reverseCell(int x, int y) throws GameException;
    void reverseCell(Point point) throws GameException;
}
