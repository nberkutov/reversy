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

    @Override
    public String toString() {
        return "Point(" +
                +x +
                ", " + y +
                ')';
    }
}
