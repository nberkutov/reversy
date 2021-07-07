package controller;

import exception.GameException;
import lombok.Data;
import models.Board;
import models.Cell;
import models.Point;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
public class BoardController {
    private final Board board;
    private int[] minX;
    private int[] maxX;
    private int[] minY;
    private int[] maxY;

    public BoardController(Board board) {
        minX = new int[8];
        maxX = new int[8];
        minY = new int[8];
        maxY = new int[8];
        Arrays.fill(minX, -1);
        Arrays.fill(maxX, -1);
        Arrays.fill(minY, -1);
        Arrays.fill(maxY, -1);

        minX[2] = 3;
        maxX[2] = 5;
        minX[3] = 3;
        maxX[3] = 5;

        minY[2] = 3;
        maxY[2] = 5;
        minY[3] = 3;
        maxY[3] = 5;

        this.board = board;
    }

    public void makeMove(Point point, Cell cell) {
        throw new NotImplementedException();
    }

    final int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1};
    final int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0};
    final int[] d = {-1, 0, 1};

    boolean isInside(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    boolean isInside(Point p) {
        return isInside(p.getX(), p.getY());
    }

    public Set<Point> getSurroundingMoves() throws GameException {
        Set<Point> moves = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            if (minX[i] != -1) {
                for (int j = 0; j < d.length; j++) {
                    Point p = new Point(minX[i], i + d[j]);
                    if (isInside(p) && board.getCell(p) == Cell.EMPTY) {
                        moves.add(p);
                    }
                }
            }
            if (minY[i] != -1) {
                for (int j = 0; j < d.length; j++) {
                    Point p = new Point(i + d[j], minY[i]);
                    if (isInside(p) && board.getCell(p) == Cell.EMPTY) {
                        moves.add(p);
                    }
                }
            }
            if (maxX[i] != -1) {
                for (int j = 0; j < d.length; j++) {
                    Point p = new Point(maxX[i], i + d[j]);
                    if (isInside(p) && board.getCell(p) == Cell.EMPTY) {
                        moves.add(p);
                    }
                }
            }
            if (maxY[i] != -1) {
                for (int j = 0; j < d.length; j++) {
                    Point p = new Point(i + d[j], maxY[i]);
                    if (isInside(p) && board.getCell(p) == Cell.EMPTY) {
                        moves.add(p);
                    }
                }
            }
        }
        System.out.println(moves.size());
        return moves;
    }

    public static void main(String[] args) {
        BoardController boardController = new BoardController(new Board());
        try {
            for (Point p : boardController.getSurroundingMoves()) {
                System.out.println(p);
            }
        } catch (GameException e) {
            e.printStackTrace();
        }
    }
}
