package org.example.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.dto.response.GameResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.models.base.interfaces.GameBoard;

import java.util.List;

@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class ReplayResponse implements GameResponse {
    private final GameBoard start;
    private final List<MoveResponse> moves;
    private final GameResultResponse result;
    private final PlayerResponse white;
    private final PlayerResponse black;
}
