package models.board;

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

    @Override
    public String toString() {
        return "Point(" +
                +x +
                ", " + y +
                ')';
    }
}
