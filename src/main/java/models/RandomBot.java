package models;

import services.BoardService;
import exception.GameException;
import models.base.PlayerColor;

import java.util.*;

public class RandomBot extends Player {

    public RandomBot(long id) {
        super(id);
    }

    public RandomBot(long id, PlayerColor color, BoardService boardService) {
        super(id, color);
    }

    @Override
    public void nextMove() throws GameException {
//        List<Point> points = boardService.getAvailableMoves(color);
//        try {
//            Point move = points.get(new Random().nextInt(points.size()));
//
//            boardService.makeMove(move, color);
//        }catch (IllegalArgumentException e){
//            System.out.println(e);
//        }
    }

}
