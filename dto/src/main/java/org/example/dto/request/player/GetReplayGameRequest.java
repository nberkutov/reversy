package org.example.dto.request.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.request.GameRequest;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class GetReplayGameRequest implements GameRequest {
    private final long gameId;
}
