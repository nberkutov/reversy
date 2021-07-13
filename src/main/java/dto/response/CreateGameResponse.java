package dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import models.game.Game;
import models.player.Player;
import models.base.PlayerColor;

@NoArgsConstructor
@AllArgsConstructor
public class CreateGameResponse extends GameResponse {
    private int idGame;
    private PlayerColor color;

    public static CreateGameResponse toDto(Game game, Player player) {
        return new CreateGameResponse(game.getId(), player.getColor());
    }
}
