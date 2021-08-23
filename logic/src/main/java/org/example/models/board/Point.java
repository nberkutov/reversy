package org.example.models.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "point")
public class Point implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private int x;
    @Column
    private int y;

    public Point(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public double distance(final Point pt) {
        final double px = pt.getX() - getX();
        final double py = pt.getY() - getY();
        return Math.sqrt(px * px + py * py);
    }

    public double distanceSq(final Point pt) {
        final double px = pt.getX() - getX();
        final double py = pt.getY() - getY();
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
