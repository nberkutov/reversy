package controller;

import lombok.Data;
import models.Board;
import models.Cell;
import models.Point;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

@Data
public class BoardController {
    private final Board board;

    public BoardController(Board board) {
        this.board = board;
    }

    public void makeMove(Point point, Cell cell) {
        throw new NotImplementedException();
    }

    public Set<Point> getAvailableMoves(Cell cell) {
        throw new NotImplementedException();
    }
}
