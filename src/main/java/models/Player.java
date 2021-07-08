package models;

import controller.BoardController;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.PlayerColor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private long id;
    protected PlayerColor color;
    protected BoardController boardController;

    public Player(long id) {
        this.id = id;
    }

    public void nextMove() throws GameException {
        throw new NotImplementedException();
    }
}
