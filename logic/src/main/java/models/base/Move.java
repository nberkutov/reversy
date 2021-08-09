package models.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.board.Point;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Move implements Serializable {
    private PlayerColor color;
    private Point point;
}
