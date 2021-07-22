package models.player;

import exception.GameException;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;
import java.util.Scanner;

public class HumanConsolePayer extends Player {
    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public Point move(GameBoard board) throws GameException {
        while (true) {
            System.out.print("Ваш ход (два числа через пробел): ");
            String input = scanner.nextLine();
            String[] args = input.trim().split("[ ]+");
            try {
                int x = Integer.parseInt(args[0]) - 1;
                int y = Integer.parseInt(args[1]) - 1;
                Point move = new Point(x, y);
                System.out.println("move " + x + " " + y);
                List<Point> availableMoves = BoardService.getAvailableMoves(board, color);
                if (availableMoves.contains(move)) {
                    return move;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Неверный формат данных.");
            }
            System.out.println("Неверный ход.");
        }
    }
}
