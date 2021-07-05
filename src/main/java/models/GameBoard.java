package models;

import java.util.Set;

interface GameBoard {
    Cell getCell(int x, int y);
    Cell getCell(Point point);
    void setCell(int x, int y, Cell cell);
    void setCell(Point point, Cell cell);
    Set<Point> getCellInAllDirection(Point point, Cell cell);
    long getCountCell(Cell cell);
    void reverseCell(int x, int y);
    void reverseCell(Point point);

}
