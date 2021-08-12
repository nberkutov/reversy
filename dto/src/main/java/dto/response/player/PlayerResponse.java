package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode
@Data
@AllArgsConstructor
public class PlayerResponse implements GameResponse {
    private final String nickname;
}
