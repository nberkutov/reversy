package dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.base.PlayerColor;
import models.game.Game;
import models.player.Player;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameResponse extends GameResponse {
    private int gameId;
    private PlayerColor color;

    public static CreateGameResponse toDto(Game game, Player player) {
        return new CreateGameResponse(game.getId(), player.getColor());
    }
}
