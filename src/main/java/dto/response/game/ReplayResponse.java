package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.*;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.game.Game;
import models.game.Move;

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

    public static ReplayResponse toDto(final Game game) {
        PlayerResponse white = PlayerResponse.toDto(game.getWhiteUser());
        PlayerResponse black = PlayerResponse.toDto(game.getBlackUser());
        GameResultResponse result = GameResultResponse.toDto(game.getResult());
        return new ReplayResponse(new Board(), game.getMoves(), result, white, black);
    }
}
