package dto.request.room;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetRoomsRequest extends GameRequest {
    private final boolean needClose;
    private final int limit;

    public GetRoomsRequest() {
        this.needClose = true;
        this.limit = 100;
    }
}
