package models;

import services.BoardService;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private long id;
    protected PlayerColor color;
    protected BoardService boardService;

    public Player(long id) {
        this.id = id;
    }

    public void nextMove() throws GameException {
        throw new NotImplementedException();
    }
}
