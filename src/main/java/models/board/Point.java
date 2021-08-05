package models.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point implements Serializable {
    private int x;
    private int y;

    public double distance(Point pt) {
        double px = pt.getX() - getX();
        double py = pt.getY() - getY();
        return Math.sqrt(px * px + py * py);
    }

    public double distanceSq(Point pt) {
        double px = pt.getX() - getX();
        double py = pt.getY() - getY();
        return px * px + py * py;
    }

    @Override
    public String toString() {
        return "Point(" +
                +x +
                ", " + y +
                ')';
    }

}
