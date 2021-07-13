package dto.response.player;

import controllers.commands.CommandResponse;
import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Player;

import static services.BaseService.GSON;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerResponse extends GameResponse {
    private long id;

    public static CreatePlayerResponse toDto(final Player player) {
        return new CreatePlayerResponse(player.getId());
    }

}
