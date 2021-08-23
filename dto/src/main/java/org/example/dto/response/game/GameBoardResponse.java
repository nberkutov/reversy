package org.example.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.response.GameResponse;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;


@Data
@EqualsAndHashCode
@AllArgsConstructor
public class GameBoardResponse implements GameResponse {
    private final long gameId;
    private final GameState state;
    private final GameBoard board;
}
