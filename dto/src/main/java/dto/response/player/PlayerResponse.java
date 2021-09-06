package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerResponse implements GameResponse {
    private String nickname;
}
