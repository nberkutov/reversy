package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.*;
import models.base.Move;
import models.base.interfaces.GameBoard;

import java.util.Deque;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReplayResponse extends GameResponse {
    private GameBoard start;
    private Deque<Move> moves;
    private GameResultResponse result;
    private PlayerResponse white;
    private PlayerResponse black;

}
