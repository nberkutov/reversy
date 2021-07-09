package services;

import exception.GameErrorCode;
import exception.GameException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.*;
import models.base.Cell;
import models.base.GameState;
import models.base.PlayerColor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static models.Board.BOARD_SIZE;

@Data
@Slf4j
public class BoardService extends BaseService {

    public static void makeMove(Game game, Point point, PlayerColor color) throws GameException {
        checkGame(game);
        checkPlayerColor(color);
        makeMoveBoard(game.getBoard(), point, Cell.valueOf(color));
    }

    public static void makeMoveBoard(Board board, Point point, Cell cell) throws GameException {
        List<Point> moves = getCellInAllDirection(board, point, cell);

        if (moves.isEmpty()) {
            throw new GameException(GameErrorCode.INVALID_MOVE);
        }

        Set<Point> pointsForReverse = new HashSet<>();
        for (Point target : moves) {
            pointsForReverse.addAll(getPointsForReverse(point, target));
        }
        board.reverseCellAll(pointsForReverse);
        board.setCell(point, cell);
    }

    public int getCountWhite(Board board) {
        return board.getCountWhite();
    }

    public int getCountBlack(Board board) {
        return board.getCountBlack();
    }

    public static int getCountEmpty(Board board) {
        return board.getCountEmpty();
    }

    public static boolean isPossibleMove(Board board, Player player) throws GameException {
        if (player == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
        return !getAvailableMoves(board, player.getColor()).isEmpty();
    }

    public static List<Point> getAvailableMoves(Board board, PlayerColor color) throws GameException {
        checkPlayerColor(color);
        return getAvailableMoves(board, Cell.valueOf(color));
    }

    public static List<Point> getAvailableMoves(Board board, Cell cell) throws GameException {
        board.checkCell(cell);
        checkCellOnEmpty(board, cell);
        Set<Point> points = new HashSet<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Point checkPoint = new Point(i, j);
                if (isCellEmpty(board, checkPoint) && !getCellInAllDirection(board, checkPoint, cell).isEmpty()) {
                    points.add(checkPoint);
                }
            }
        }
        return new ArrayList<>(points);
    }

    private static List<Point> getCellInAllDirection(Board board, Point point, Cell cell) throws GameException {
        board.checkPoint(point);
        checkCellOnEmpty(board, cell);
        Set<Point> points = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                Point checkPoint = new Point(point.getX() + i, point.getY() + j);
                if (board.validation(checkPoint)
                        && !isCellEmpty(board, checkPoint)
                        && !board.getCell(checkPoint).equals(cell)) {
                    Point found = getPointInDirection(board, checkPoint, cell, i, j);
                    if (found != null) {
                        points.add(found);
                    }
                }
            }
        }
        return new ArrayList<>(points);
    }

    private static Set<Point> getPointsForReverse(Point point, Point target) {
        Set<Point> points = new HashSet<>();
        Point p = new Point(point.getX(), point.getY());
        while (!p.equals(target)) {
            if (p.getX() < target.getX()) {
                p.setX(p.getX() + 1);
            } else if (p.getX() > target.getX()) {
                p.setX(p.getX() - 1);
            }
            if (p.getY() < target.getY()) {
                p.setY(p.getY() + 1);
            } else if (p.getY() > target.getY()) {
                p.setY(p.getY() - 1);
            }
            points.add(new Point(p.getX(), p.getY()));
        }
        points.remove(target);
        return points;
    }

    private static Point getPointInDirection(Board board, Point point, Cell cell, int difX, int difY) throws GameException {
        Point p = new Point(point.getX(), point.getY());
        do {
            p.setX(p.getX() + difX);
            p.setY(p.getY() + difY);
            if (!board.validation(p) || isCellEmpty(board, p)) {
                return null;
            }
        } while (!board.getCell(p).equals(cell));
        return p;
    }

    private static void checkGame(Game game) throws GameException {
        if (game == null) {
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
    }

    private static void checkCellOnEmpty(Board board, Cell cell) throws GameException {
        board.checkCell(cell);
        if (isCellEmpty(board, cell)) {
            //log.error("Bad checkCellOnEmpty", new GameException(GameErrorCode.INVALID_CELL));
            throw new GameException(GameErrorCode.INVALID_CELL);
        }
    }

    private static void checkPlayerColor(PlayerColor color) throws GameException {
        if (color == null) {
            log.error("Bad checkPlayerColor", new GameException(GameErrorCode.INVALID_PLAYER_COLOR));
            throw new GameException(GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

    private static boolean isCellEmpty(Board board, Point point) throws GameException {
        return isCellEmpty(board, board.getCell(point));
    }

    private static boolean isCellEmpty(Board board, Cell cell) {
        return cell == Cell.EMPTY;
    }


}
