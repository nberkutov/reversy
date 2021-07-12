package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    private int x;
    private int y;

   /* public Set<Point> getNeighbours(int boardSize) {
        Set<Point> neightbours = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j ==0) {
                    continue;
                }

            }
        }
        return neightbours;
    }*/

    /*public boolean isInside(int boardSize) {
        if (x >= 0);
        return false;
    }*/

    public Point getLeft() {
        return new Point(x - 1, y);
    }

    public Point getLeftUp(Point point) {
        return new Point(x - 1, y - 1);
    }

    public Point getUp(Point point) {
        return new Point(x, y - 1);
    }

    public Point getRightUp(Point point) {
        return new Point(x + 1, y - 1);
    }

    public Point getRight(Point point) {
        return new Point(x + 1, y);
    }

    public Point getRightDown(Point point) {
        return new Point(x + 1, y + 1);
    }

    public Point getDown(Point point) {
        return new Point(x, y + 1);
    }

    public Point getLeftDown(Point point) {
        return new Point(x - 1, y + 1);
    }

    @Override
    public String toString() {
        return "Point(" +
                + x +
                ", " + y +
                ')';
    }
}
