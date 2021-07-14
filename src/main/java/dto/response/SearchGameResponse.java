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
public class SearchGameResponse extends GameResponse {
    private int gameId;
    private PlayerColor color;

    public static SearchGameResponse toDto(Game game, Player player) {
        return new SearchGameResponse(game.getId(), player.getColor());
    }
}

