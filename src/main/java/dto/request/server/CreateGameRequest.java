package dto.request.server;

import dto.request.player.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.player.Player;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameRequest extends GameRequest {
    private int firstPlayerId;
    private int secondPlayerId;

    public static CreateGameRequest toDto(Player first, Player second) {
        return new CreateGameRequest(first.getId(), second.getId());
    }
}
