package dto.response.room;

import dto.response.GameResponse;
import dto.response.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.game.Room;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse extends GameResponse {
    private int id;
    private PlayerResponse blackPlayer;
    private PlayerResponse whitePlayer;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.blackPlayer = PlayerResponse.toDto(room.getBlackUser());
        this.whitePlayer = PlayerResponse.toDto(room.getWhiteUser());
    }

    public static RoomResponse toDto(final Room room) {
        return new RoomResponse(room);
    }
}
