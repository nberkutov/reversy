package dto.request.player;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class GetReplayGameRequest implements GameRequest {
    private final int gameId;
}
