package org.example.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.response.GameResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.models.base.PlayerColor;


@Data
@EqualsAndHashCode
@AllArgsConstructor
public class CreateGameResponse implements GameResponse {
    private final long gameId;
    private final PlayerColor color;
    private final PlayerResponse opponent;
}

