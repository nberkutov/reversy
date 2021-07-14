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

    @Override
    public String toString() {
        return "Point(" +
                +x +
                ", " + y +
                ')';
    }

}
