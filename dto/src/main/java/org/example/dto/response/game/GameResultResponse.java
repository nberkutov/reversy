package org.example.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.dto.response.GameResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.models.base.GameResultState;

@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class GameResultResponse implements GameResponse {
    private final GameResultState state;
    private final PlayerResponse winner;
}
