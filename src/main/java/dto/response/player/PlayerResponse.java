package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.player.Player;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerResponse extends GameResponse {
    private String nickname;

    public static PlayerResponse toDto(Player player) {
        if (player == null) {
            return null;
        }
        return new PlayerResponse(player.getNickname());
    }
}
