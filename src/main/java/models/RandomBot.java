package models;

import controller.BoardController;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.base.Cell;
import models.base.PlayerColor;

import java.util.*;

public class RandomBot extends Player {

    public RandomBot(long id) {
        super(id);
    }

    public RandomBot(long id, PlayerColor color, BoardController boardController) {
        super(id, color, boardController);
    }

    @Override
    public void nextMove() throws GameException {
        List<Point> points = boardController.getAvailableMoves(color);
        try {
            Point move = points.get(new Random().nextInt(points.size()));

            boardController.makeMove(move, color);
        }catch (IllegalArgumentException e){
            System.out.println(e);
        }
    }

}
