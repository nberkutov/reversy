package models;

import services.MoveService;
import exception.GameException;
import models.base.PlayerColor;

import java.util.*;

public class RandomBotPlayer extends Player {

    public RandomBotPlayer(long id, PlayerColor color, MoveService moveService) {
        super(id, color, moveService);
    }

    @Override
    public void nextMove() throws GameException {
        List<Point> points = moveService.getAvailableMoves(color);
        try {
            Point move = points.get(new Random().nextInt(points.size()));

            moveService.makeMove(move, color);
        }catch (IllegalArgumentException e){
            System.out.println(e);
        }
    }

}
