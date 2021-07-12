package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerResponse extends GameResponse {
    private long id;

    public static CreatePlayerResponse toDto(Player player) {
        return new CreatePlayerResponse(player.getId());
    }
}
