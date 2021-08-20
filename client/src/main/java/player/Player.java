package player;

import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import strategy.Strategy;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Player {
    protected PlayerColor color;
    private String nickname;
    protected Strategy strategy;

    public Player(final String nickname, final Strategy strategy) {
        this.nickname = nickname;
        this.strategy = strategy;
        strategy.setColor(color);
    }

    public abstract Point move(GameBoard board) throws ServerException;

    public void setColor(final PlayerColor color) {
        this.color = color;
        strategy.setColor(color);
    }
}
