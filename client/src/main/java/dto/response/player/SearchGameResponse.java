package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchGameResponse extends GameResponse {
    private int gameId;
    private PlayerColor color;

}

