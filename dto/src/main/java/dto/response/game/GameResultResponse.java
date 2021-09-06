package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.GameResultState;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultResponse implements GameResponse {
    private GameResultState state;
    private PlayerResponse winner;

}
