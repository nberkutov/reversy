package org.example.dto.request.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.request.GameRequest;
import org.example.models.board.Point;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class MovePlayerRequest implements GameRequest {
    private final long gameId;
    private final Point point;

    public static MovePlayerRequest toDto(final long gameId, final Point point) {
        return new MovePlayerRequest(gameId, point);
    }
}
