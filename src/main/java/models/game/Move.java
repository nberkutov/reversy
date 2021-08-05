package models.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.PlayerColor;
import models.board.Point;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Move implements Serializable {
    private PlayerColor color;
    private Point point;

    public static Move create(PlayerColor color, Point point) {
        return new Move(color, point);
    }

}
