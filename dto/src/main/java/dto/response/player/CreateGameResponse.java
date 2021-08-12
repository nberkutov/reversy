package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.base.PlayerColor;


@Data
@EqualsAndHashCode
@AllArgsConstructor
public class CreateGameResponse implements GameResponse {
    private final int gameId;
    private final PlayerColor color;

}

