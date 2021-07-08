package models;

import exception.GameException;
import models.base.Cell;
import services.MoveService;

import java.util.Scanner;

public class HumanPlayer extends Player {
    private static final Scanner scanner = new Scanner(System.in);

    public HumanPlayer(long id, MoveService moveService) {
        super(id, moveService);
    }

    @Override
    public void nextMove() throws GameException {
        int x = -1;
        int y = -1;
        while (x < 0 || x > 8 || y < -1 || y > 8) {
            System.out.print("> x: ");
            x = scanner.nextInt();
            System.out.println();
            System.out.print("> y: ");
            y = scanner.nextInt();
            System.out.println();
            moveService.makeMove(new Point(x, y), Cell.valueOf(color));
        }
    }
}
