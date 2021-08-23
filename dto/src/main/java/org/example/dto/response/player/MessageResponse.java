package org.example.dto.response.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.response.GameResponse;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class MessageResponse implements GameResponse {
    private final String message;


}
