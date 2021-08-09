package dto.response.room;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse extends GameResponse {
    private int id;
    private PlayerResponse blackPlayer;
    private PlayerResponse whitePlayer;
}
