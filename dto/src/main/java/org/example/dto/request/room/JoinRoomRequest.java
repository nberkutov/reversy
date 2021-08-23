package org.example.dto.request.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.request.GameRequest;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class JoinRoomRequest implements GameRequest {
    private final long id;
}
