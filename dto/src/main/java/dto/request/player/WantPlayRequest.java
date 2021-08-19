package dto.request.player;

import dto.request.GameRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;


@Data
@EqualsAndHashCode
@AllArgsConstructor
public class WantPlayRequest implements GameRequest {
    private PlayerColor color;
}
