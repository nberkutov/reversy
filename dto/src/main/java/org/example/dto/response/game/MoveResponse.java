package org.example.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.example.models.base.PlayerColor;
@AllArgsConstructor
@Data
@ToString
public class MoveResponse {
    private final PlayerColor color;
    private final PointResponse point;
}
