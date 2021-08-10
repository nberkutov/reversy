package models.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.board.Point;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Move implements Serializable {
    private final PlayerColor color;
    private final Point point;

    public static Move create(PlayerColor color, Point point) {
        return new Move(color, point);
    }
}
