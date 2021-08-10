package dto.request.room;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.base.PlayerColor;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class CreateRoomRequest implements GameRequest {
    private final PlayerColor color;

    public CreateRoomRequest() {
        this.color = PlayerColor.NONE;
    }
}
