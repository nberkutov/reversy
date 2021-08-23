package org.example.dto.request.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.request.GameRequest;
import org.example.models.base.PlayerColor;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class CreateRoomRequest implements GameRequest {
    private final PlayerColor color;

    public CreateRoomRequest() {
        this.color = PlayerColor.NONE;
    }
}
