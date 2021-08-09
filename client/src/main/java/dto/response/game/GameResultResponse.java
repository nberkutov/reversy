package dto.response.game;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.GameResultState;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultResponse extends GameResponse {
    private GameResultState state;
    private PlayerResponse winner;

}
