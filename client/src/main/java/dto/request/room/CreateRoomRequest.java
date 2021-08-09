package dto.request.room;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.base.PlayerColor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CreateRoomRequest extends GameRequest {
    private PlayerColor color;

    public CreateRoomRequest() {
        this.color = PlayerColor.NONE;
    }
}
