package dto.response.room;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class ListRoomResponse implements GameResponse {
    private final List<RoomResponse> list;

}
