package dto.request.player;

import dto.request.GameRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WantPlayRequest extends GameRequest {
}
