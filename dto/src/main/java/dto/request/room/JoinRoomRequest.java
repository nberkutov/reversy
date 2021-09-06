package dto.request.room;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class JoinRoomRequest implements GameRequest {
    private final int id;
}
