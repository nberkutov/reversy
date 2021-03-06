package logic;

import exception.GameErrorCode;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static models.GameProperties.BOARD_SIZE;

@Slf4j
public class BoardLogic {

    private BoardLogic() {
    }

    /**
     * Функция ищет всевозможные ходы, а в последствиии переворчивает фишки
     * Если нет возможных ходов, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @param point - точка куда походил игрок
     * @param cell  - фишка
     */
    public static void makeMove(final GameBoard board, final Point point, final Cell cell) throws ServerException {
        boardIsNotNull(board);
        checkPointIsInside(point);
        checkCellIsEmpty(cell);
        final List<Point> moves = getCellInAllDirection(board, point, cell);

        if (moves.isEmpty()) {
            throw new ServerException(GameErrorCode.INVALID_MOVE);
        }

        final Set<Point> pointsForReverse = new HashSet<>();
        for (final Point target : moves) {
            pointsForReverse.addAll(getPointsForReverse(point, target));
        }
        board.reverseCells(pointsForReverse);
        board.setCell(point, cell);
    }

    /**
     * Функция получения количества белых фишек
     *
     * @param board - Игровое поле
     */
    public static int getCountWhite(final GameBoard board) {
        return board.getCountWhiteCells();
    }

    /**
     * Функция получения количества чёрных фишек
     *
     * @param board - Игровое поле
     */
    public static int getCountBlack(final GameBoard board) {
        return board.getCountBlackCells();
    }

    public static int getCountCellByPlayerColor(final GameBoard board, final PlayerColor color) throws ServerException {
        boardIsNotNull(board);
        colorIsNotNull(color);
        colorIsNotNone(color);
        if (color == PlayerColor.BLACK) {
            return getCountBlack(board);
        }
        return getCountWhite(board);
    }

    /**
     * Функция получения количества пустых полей
     *
     * @param board - Игровое поле
     */
    public static int getCountEmpty(final GameBoard board) {
        return board.getCountEmpty();
    }

    /**
     * Функция, которая определяет, возможны ли вообще ещё ходы
     * Если player равен null, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @return boolean
     */
    public static boolean isNotPossiblePlayOnBoard(final GameBoard board) throws ServerException {
        boardIsNotNull(board);
        if (board.getCountEmpty() == 0
                || board.getCountBlackCells() == 0
                || board.getCountWhiteCells() == 0) {
            return true;
        }
        return getAvailableMoves(board, PlayerColor.BLACK).isEmpty()
                && getAvailableMoves(board, PlayerColor.WHITE).isEmpty();
    }


    /**
     * Функция, которая определяет, может ли ходить игрок
     * Если player равен null, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @return boolean
     */
    public static boolean canMove(final GameBoard board, final PlayerColor color) throws ServerException {
        colorIsNotNull(color);
        return !getAvailableMoves(board, color).isEmpty();
    }


    /**
     * Функция, которая находит все возможные ходы, относительно цвета игрока
     * Если color равен null, то выбрасывает GameException.
     *
     * @param board - Игровое поле
     * @param color - цвет игрока
     * @return List<Point>
     */
    public static List<Point> getAvailableMoves(final GameBoard board, final PlayerColor color) throws ServerException {
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
    public static List<Point> getAvailableMoves(final GameBoard board, final Cell cell) throws ServerException {
        boardIsNotNull(board);
        checkCellIsEmpty(cell);
        final Set<Point> points = new HashSet<>();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
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
    public static List<Point> getCellInAllDirection(final GameBoard board, final Point point, final Cell cell) throws ServerException {
        boardIsNotNull(board);
        checkPointIsInside(point);
        checkCellIsEmpty(cell);
        final Set<Point> points = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                final Point checkPoint = new Point(point.getX() + i, point.getY() + j);
                if (board.validate(checkPoint)
                        && !isCellEmpty(board, checkPoint)
                        && !board.getCell(checkPoint).equals(cell)) {
                    final Point found = getPointInDirection(board, checkPoint, cell, i, j);
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
    private static Set<Point> getPointsForReverse(final Point point, final Point target) throws ServerException {
        checkPointIsInside(point);
        checkPointIsInside(target);
        final Set<Point> points = new HashSet<>();
        final Point p = new Point(point.getX(), point.getY());
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
    private static Point getPointInDirection(final GameBoard board, final Point point, final Cell cell, final int difX, final int difY) throws ServerException {
        boardIsNotNull(board);
        checkPointIsInside(point);
        checkCellIsEmpty(cell);
        final Point p = new Point(point.getX(), point.getY());
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
    private static void checkCellIsEmpty(final Cell cell) throws ServerException {
        if (cell == null || cell == Cell.EMPTY) {
            log.error("Bad checkCellOnEmpty", new ServerException(GameErrorCode.INVALID_CELL));
            throw new ServerException(GameErrorCode.INVALID_CELL);
        }
    }

    /**
     * Функция провероки
     * Если color равен null, то выбрасывает GameException.
     *
     * @param color - цвет игрока
     */
    private static void colorIsNotNull(final PlayerColor color) throws ServerException {
        if (color == null) {
            log.error("Bad checkPlayerColor", new ServerException(GameErrorCode.INVALID_PLAYER_COLOR));
            throw new ServerException(GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

    private static void colorIsNotNone(final PlayerColor color) throws ServerException {
        if (color == PlayerColor.NONE) {
            throw new ServerException(GameErrorCode.INVALID_PLAYER_COLOR);
        }
    }

    /**
     * Функция провероки
     * Если board равен null, то выбрасывает GameException.
     *
     * @param board - игровое поле
     */
    private static void boardIsNotNull(final GameBoard board) throws ServerException {
        if (board == null) {
            log.error("Bad checkBoard", new ServerException(GameErrorCode.BOARD_NOT_FOUND));
            throw new ServerException(GameErrorCode.BOARD_NOT_FOUND);
        }
    }

    /**
     * Функция для определения поля на пустоту
     *
     * @param board - игровое поле
     * @param point - точка
     */
    private static boolean isCellEmpty(final GameBoard board, final Point point) throws ServerException {
        return board.getCell(point) == Cell.EMPTY;
    }

    /**
     * Функция провероки
     * Если point равен null, то выбрасывает GameException.
     *
     * @param point - игровое поле
     */
    public static void checkPointIsInside(final Point point) throws ServerException {
        if (point != null
                && point.getX() >= 0
                && point.getY() >= 0
                && point.getX() < BOARD_SIZE
                && point.getY() < BOARD_SIZE) {
            return;
        }
        log.error("Bad checkPoint", new ServerException(GameErrorCode.BAD_POINT));
        throw new ServerException(GameErrorCode.BAD_POINT);
    }
}
