package org.example.dto.response.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.response.GameResponse;
import org.example.dto.response.player.PlayerResponse;


@EqualsAndHashCode
@Data
@AllArgsConstructor
public class RoomResponse implements GameResponse {
    private final long id;
    private final PlayerResponse blackPlayer;
    private final PlayerResponse whitePlayer;

}
