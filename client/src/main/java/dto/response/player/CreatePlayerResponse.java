package dto.response.player;

import dto.response.GameResponse;
import lombok.*;


@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerResponse extends GameResponse {
    private int id;
    private String nickname;
}
