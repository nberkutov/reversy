package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.player.Player;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerResponse extends GameResponse {
    private int id;

    public static CreatePlayerResponse toDto(final Player player) {
        return new CreatePlayerResponse(player.getId());
    }

}
