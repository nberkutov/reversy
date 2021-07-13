package services;

import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.Board;
import models.Game;
import models.Player;
import models.Point;
import models.base.Cell;
import models.base.PlayerColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static models.Board.BOARD_SIZE;

@Slf4j
public class BoardService extends BaseService {

    /**
     * Совершает ход на игровой доске.
     *
     * @param game  - Игра
     * @param point - точка куда походил игрок
     * @param color - цвет игрока
     */
    public static void makeMove(final Game game, final Point point, final PlayerColor color) throws GameException {
        gameIsNotNull(game);
        colorIsNotNull(color);
        makeMove(game.getBoard(), point, Cell.valueOf(color));
    }

    /**
     * Совершает ход на игровой доске.
     *
     * @param board - Игровое поле
     * @param point - точка куда походил игрок
     * @param cell  - фишка
     */
    public static void makeMove(final Board board, final Point point, final Cell cell) throws GameException {
        makeMoveBoard(board, point, cell);
    }

    /**
     * Функция ищет всевозможные ходы, а в последствиии переворчивает фишки
     * Если нет возможных ходов, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @param point - точка куда походил игрок
     * @param cell  - фишка
     */
    private static void makeMoveBoard(final Board board, final Point point, final Cell cell) throws GameException {
        boardIsNotNull(board);
        checkPointIsInside(point);
        checkCellIsEmpty(cell);
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

    /**
     * Функция получения количества белых фишек
     *
     * @param board - Игровое поле
     */
    public static int getCountWhite(final Board board) {
        return board.getCountWhiteCells();
    }

    /**
     * Функция получения количества чёрных фишек
     *
     * @param board - Игровое поле
     */
    public static int getCountBlack(final Board board) {
        return board.getCountBlackCells();
    }


    /**
     * Функция получения количества пустых полей
     *
     * @param board - Игровое поле
     */
    public static int getCountEmpty(final Board board) {
        return board.getCountEmpty();
    }

    /**
     * Функция, которая определяет, может ли ходить игрок
     * Если player равен null, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @return boolean
     */
    public static boolean hasPossibleMove(final Board board, final Player player) throws GameException {
        playerIsNotNull(player);
        return !getAvailableMoves(board, player.getColor()).isEmpty();
    }

    /**
     * Функция, которая находит все возможные ходы, относительно цвета игрока
     * Если color равен null, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @param color - цвет игрока
     * @return List<Point>
     */
    public static List<Point> getAvailableMoves(final Board board, final PlayerColor color) throws GameException {
        colorIsNotNull(color);
        return getAvailableMoves(board, Cell.valueOf(color));
    }

    /**
     * Функция, которая находит все возможные ходы, относительно цвета фишки
     * Если board равен null или cell равен null/Cell.Empty, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @param cell  - цвец фишки
     * @return List<Point>
     */
    public static List<Point> getAvailableMoves(final Board board, final Cell cell) throws GameException {
        boardIsNotNull(board);
        checkCellIsEmpty(cell);
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

    /**
     * Функция, которая находит во всех направлениях ближайшую одинаковую фишку, чтоб потом найти промежуточные фишки и их перевернуть
     * Если board равен null или cell равен null/Cell.Empty, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @param cell  - цвец фишки
     * @return List<Point>
     */
    public static List<Point> getCellInAllDirection(final Board board, final Point point, final Cell cell) throws GameException {
        boardIsNotNull(board);
        checkPointIsInside(point);
        checkCellIsEmpty(cell);
        Set<Point> points = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                Point checkPoint = new Point(point.getX() + i, point.getY() + j);
                if (board.validate(checkPoint)
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

    /**
     * Функция для нахождения промежуточных точек
     *
     * @param point- начальная точка
     * @param target - конечная точка
     * @return Set<Point>
     */
    private static Set<Point> getPointsForReverse(final Point point, final Point target) throws GameException {
        checkPointIsInside(point);
        checkPointIsInside(target);
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

    /**
     * Функция, которая находит в одном направлениях ближайшую одинаковую фишку, чтоб потом найти промежуточные фишки и их перевернуть
     *
     * @param board - Игровое поле
     * @param cell  - цвец фишки
     * @return Point
     */
    private static Point getPointInDirection(final Board board, final Point point, final Cell cell, final int difX, final int difY) throws GameException {
        boardIsNotNull(board);
        checkPointIsInside(point);
        checkCellIsEmpty(cell);
        Point p = new Point(point.getX(), point.getY());
        do {
            p.setX(p.getX() + difX);
            p.setY(p.getY() + difY);
            if (!board.validate(p) || isCellEmpty(board, p)) {
                return null;
            }
        } while (!board.getCell(p).equals(cell));
        return p;
    }


    /**
     * Функция провероки
     * Если cell равен null/Cell.Empty, то выбрасывает GameException.
     *
     * @param cell - фишка
     */
    private static void checkCellIsEmpty(final Cell cell) throws GameException {
        if (cell == null || isCellEmpty(cell)) {
            log.error("Bad checkCellOnEmpty", new GameException(GameErrorCode.INVALID_CELL));
            throw new GameException(GameErrorCode.INVALID_CELL);
        }
    }

    /**
     * Функция провероки
     * Если color равен null, то выбрасывает GameException.
     *
     * @param color - цвет игрока
     */
    private static void colorIsNotNull(final PlayerColor color) throws GameException {
        if (color == null) {
            log.error("Bad checkPlayerColor", new GameException(GameErrorCode.INVALID_PLAYER_COLOR));
            throw new GameException(GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

    /**
     * Функция провероки
     * Если board равен null, то выбрасывает GameException.
     *
     * @param board - игровое поле
     */
    private static void boardIsNotNull(final Board board) throws GameException {
        if (board == null) {
            log.error("Bad checkBoard", new GameException(GameErrorCode.BOARD_NOT_FOUND));
            throw new GameException(GameErrorCode.BOARD_NOT_FOUND);
        }
    }

    /**
     * Функция для определения поля на пустоту
     *
     * @param board - игровое поле
     * @param point - точка
     */
    private static boolean isCellEmpty(final Board board, final Point point) throws GameException {
        return isCellEmpty(board.getCell(point));
    }

    /**
     * Функция для определения поля на пустоту
     *
     * @param cell - фишка
     */
    private static boolean isCellEmpty(final Cell cell) {
        return cell == Cell.EMPTY;
    }
}
