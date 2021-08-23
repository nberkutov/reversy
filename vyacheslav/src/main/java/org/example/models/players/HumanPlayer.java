package org.example.models.players;

import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.Player;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;

import java.util.List;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private static final Scanner scanner = new Scanner(System.in);

    public HumanPlayer(final String nickname) {
        super(nickname);
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        while (true) {
            System.out.print("Ваш ход (два числа через пробел): ");
            final String input = scanner.nextLine();
            final String[] args = input.trim().split("[ ]+");
            try {
                final int x = Integer.parseInt(args[0]) - 1;
                final int y = Integer.parseInt(args[1]) - 1;
                final Point move = new Point(x, y);
                System.out.println("move " + x + " " + y);
                final List<Point> availableMoves = BoardLogic.getAvailableMoves(board, color);
                if (availableMoves.contains(move)) {
                    return move;
                }
            } catch (final NumberFormatException | ServerException ex) {
                System.out.println("Неверный формат данных.");
            }
            System.out.println("Неверный ход.");
        }
    }
}
