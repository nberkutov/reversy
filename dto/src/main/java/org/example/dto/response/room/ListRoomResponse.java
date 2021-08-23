package org.example.dto.response.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.response.GameResponse;

import java.util.List;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class ListRoomResponse implements GameResponse {
    private final List<RoomResponse> list;
}
