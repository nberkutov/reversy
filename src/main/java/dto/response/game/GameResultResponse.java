package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.GameResultState;
import models.game.GameResult;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultResponse extends GameResponse {
    private GameResultState state;
    private PlayerResponse winner;

    public static GameResultResponse toDto(final GameResult result) {
        PlayerResponse winner = PlayerResponse.toDto(result.getWinner());
        return new GameResultResponse(result.getResultState(), winner);
    }
}
