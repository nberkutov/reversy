package dto.request.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerRequest extends GameRequest {
    private String nickname;
}
