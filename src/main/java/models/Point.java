package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    private int x;
    private int y;

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
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
