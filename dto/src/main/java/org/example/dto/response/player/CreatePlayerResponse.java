package org.example.dto.response.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.dto.response.GameResponse;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CreatePlayerResponse implements GameResponse {
    private final long id;
    private final String nickname;

}
