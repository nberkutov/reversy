package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private long id;
    private PlayerColor color;
}
