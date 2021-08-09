package dto.response.player;

import dto.response.GameResponse;
import lombok.*;
import models.player.User;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerResponse extends GameResponse {
    private int id;
    private String nickname;

    public static CreatePlayerResponse toDto(User user) {
        return new CreatePlayerResponse(user.getId(), user.getNickname());
    }
}