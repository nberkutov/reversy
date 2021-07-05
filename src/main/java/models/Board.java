package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Slf4j
public class Board implements GameBoard {
    private static int BOARD_SIZE = 8;
    private Map<Point, Cell> mapCell;

    public Board() {
        this(init());
    }

    public Board(Map<Point, Cell> mapCell) {
        this.mapCell = mapCell;
    }

    private static Map<Point, Cell> init() {
        Map<Point, Cell> map = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                map.put(new Point(i, j), Cell.EMPTY);
            }
        }
        return map;
    }

    @Override
    public Cell getCell(int x, int y) {
        return getCell(new Point(x, y));
    }

    @Override
    public Cell getCell(Point point) {
        if (!validation(point)) {
            log.error("Bad point {}", point);
            throw new RuntimeException("Bad point");
        }
        return mapCell.get(point);
    }

    @Override
    public void setCell(int x, int y, Cell cell) {

    }

    @Override
    public void setCell(Point point, Cell cell) {

    }

    @Override
    public Set<Point> getCellInAllDirection(Point point, Cell cell) {
        return null;
    }

    @Override
    public int getCountCell(Cell cell) {
        return 0;
    }

    @Override
    public void reverseCell(int x, int y) {

    }

    @Override
    public void reverseCell(Point point) {

    }

    private boolean validation(Point point) {
        return point.getX() >= 0 && point.getY() >= 0 && point.getX() < BOARD_SIZE && point.getY() < BOARD_SIZE;
    }
}
