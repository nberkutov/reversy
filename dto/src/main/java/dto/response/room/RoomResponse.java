package dto.response.room;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode
@Data
@AllArgsConstructor
public class RoomResponse implements GameResponse {
    private final int id;
    private final PlayerResponse blackPlayer;
    private final PlayerResponse whitePlayer;

}
