package dto.request.server;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.player.User;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameRequest extends GameRequest {
    private int firstPlayerId;
    private int secondPlayerId;

    public static CreateGameRequest toDto(User first, User second) {
        return new CreateGameRequest(first.getId(), second.getId());
    }
}
