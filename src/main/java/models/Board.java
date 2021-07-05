package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
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
        map.put(new Point(3, 3), Cell.WHITE);
        map.put(new Point(3, 4), Cell.BLACK);
        map.put(new Point(4, 3), Cell.BLACK);
        map.put(new Point(4, 4), Cell.WHITE);
        return map;
    }

    @Override
    public Cell getCell(int x, int y) {
        return getCell(new Point(x, y));
    }

    @Override
    public Cell getCell(Point point) {
        checkPoint(point);
        return mapCell.get(point);
    }

    @Override
    public void setCell(int x, int y, Cell cell) {
        setCell(new Point(x, y), cell);
    }

    @Override
    public void setCell(Point point, Cell cell) {
        checkPoint(point);
        mapCell.put(point, cell);
    }

    @Override
    public Set<Point> getCellInAllDirection(Point point, Cell cell) {
        checkPoint(point);
        Set<Point> points = new HashSet<>();
        //left
        for (int x = point.getX(); x >= 0; x--) {
            Point checkPoint = new Point(x,point.getY());
            if(isCellEmpty(checkPoint)){
                break;
            }
            if (getCell(checkPoint).equals(cell)) {
                points.add(checkPoint);
                break;
            }
        }
        //left+up
        for (int x = point.getX(); x >= 0; x--) {
            for (int y = point.getY(); y >= 0; y--) {
                Point checkPoint = new Point(x,y);
                if(isCellEmpty(checkPoint)){
                    break;
                }
                if (getCell(checkPoint).equals(cell)) {
                    points.add(checkPoint);
                    break;
                }
            }
        }
        //up
        for (int y = point.getY(); y >= 0; y--) {
            Point checkPoint = new Point(point.getX(),y);
            if(isCellEmpty(checkPoint)){
                break;
            }
            if (getCell(checkPoint).equals(cell)) {
                points.add(checkPoint);
                break;
            }
        }
        //right+up
        for (int x = point.getX(); x < BOARD_SIZE; x++) {
            for (int y = point.getY(); y >= 0; y--) {
                Point checkPoint = new Point(x,y);
                if(isCellEmpty(checkPoint)){
                    break;
                }
                if (getCell(checkPoint).equals(cell)) {
                    points.add(checkPoint);
                    break;
                }
            }
        }
        //right
        for (int x = point.getX(); x < BOARD_SIZE; x++) {
            Point checkPoint = new Point(x,point.getY());
            if(isCellEmpty(checkPoint)){
                break;
            }
            if (getCell(checkPoint).equals(cell)) {
                points.add(checkPoint);
                break;
            }
        }
        //right+down
        for (int x = point.getX(); x < BOARD_SIZE; x++) {
            for (int y = point.getY(); y < BOARD_SIZE; y++) {
                Point checkPoint = new Point(x,y);
                if(isCellEmpty(checkPoint)){
                    break;
                }
                if (getCell(checkPoint).equals(cell)) {
                    points.add(checkPoint);
                    break;
                }
            }
        }
        //down
        for (int y = point.getY(); y < BOARD_SIZE; y++) {
            Point checkPoint = new Point(point.getX(),y);
            if(isCellEmpty(checkPoint)){
                break;
            }
            if (getCell(checkPoint).equals(cell)) {
                points.add(checkPoint);
                break;
            }
        }
        //left+down
        for (int x = point.getX(); x >= 0; x--) {
            for (int y = point.getY(); y < BOARD_SIZE; y++) {
                Point checkPoint = new Point(x,y);
                if(isCellEmpty(checkPoint)){
                    break;
                }
                if (getCell(checkPoint).equals(cell)) {
                    points.add(checkPoint);
                    break;
                }
            }
        }
        return points;
    }

    @Override
    public long getCountCell(Cell cell) {
        return mapCell.values().stream().filter(x -> x.equals(cell)).count();
    }

    @Override
    public void reverseCell(int x, int y) {
        reverseCell(new Point(x, y));
    }

    @Override
    public void reverseCell(Point point) {
        checkPoint(point);
        Cell cell = mapCell.get(point);
        if (cell.equals(Cell.EMPTY)) {
            log.error("Bad reverseCell {}, its cell is {}", point, cell);
            throw new RuntimeException("Bad point");
        }

        if (cell.equals(Cell.WHITE)) {
            mapCell.put(point, Cell.BLACK);
        } else {
            mapCell.put(point, Cell.WHITE);
        }
    }

    private boolean isCellEmpty(Point point){
        return getCell(point).equals(Cell.EMPTY);
    }

    private void checkPoint(Point point) {
        if (!validation(point)) {
            log.error("Bad checkPoint {}", point);
            throw new RuntimeException("Bad point");
        }
    }

    private boolean validation(Point point) {
        return point != null && point.getX() >= 0 && point.getY() >= 0 && point.getX() < BOARD_SIZE && point.getY() < BOARD_SIZE;
    }

    @Override
    public String toString() {
        return "Board{}";
    }
}
