package dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.game.Game;
import models.player.Player;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchGameResponse extends GameResponse {
    private int gameId;
    private PlayerColor color;

    public static SearchGameResponse toDto(Game game, Player player) {
        return new SearchGameResponse(game.getId(), player.getColor());
    }
}

