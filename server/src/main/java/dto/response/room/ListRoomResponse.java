package dto.response.room;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.game.Room;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListRoomResponse extends GameResponse {
    private List<RoomResponse> list;

    public static ListRoomResponse toDto(List<Room> list) {
        return new ListRoomResponse(list.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList()));
    }
}
