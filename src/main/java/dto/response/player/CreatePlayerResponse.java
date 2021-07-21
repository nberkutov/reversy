package dto.response.player;

import dto.response.GameResponse;
import lombok.*;
import models.player.Player;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerResponse extends GameResponse {
    private int id;
    private String nickname;

    public static CreatePlayerResponse toDto(Player player) {
        return new CreatePlayerResponse(player.getId(), player.getNickname());
    }
}
