package org.example.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.example.models.board.Point;

@AllArgsConstructor
@Data
@ToString
public class PointResponse {
    private final int x;
    private final int y;

    public Point to() {
        return new Point(x, y);
    }
}
