package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import models.base.Move;
import models.base.interfaces.GameBoard;

import java.util.Deque;

@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class ReplayResponse implements GameResponse {
    private final GameBoard start;
    private final Deque<Move> moves;
    private final GameResultResponse result;
    private final PlayerResponse white;
    private final PlayerResponse black;
}
