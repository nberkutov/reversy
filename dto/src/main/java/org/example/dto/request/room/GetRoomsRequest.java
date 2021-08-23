package org.example.dto.request.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.request.GameRequest;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class GetRoomsRequest implements GameRequest {
    private final boolean needClose;
    private final int limit;

    public GetRoomsRequest() {
        this.needClose = true;
        this.limit = 100;
    }
}
