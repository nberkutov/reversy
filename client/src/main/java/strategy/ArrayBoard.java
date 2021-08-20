package strategy;

import exception.GameErrorCode;
import exception.ServerException;
import models.base.Cell;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.Arrays;
import java.util.Collection;

public class ArrayBoard implements GameBoard {
    private static final int SIZE = 8;
    private final Cell[] board;

    private int whiteCells;
    private int blackCells;
    private int emptyCells;

    public ArrayBoard() {
        board = new Cell[SIZE * SIZE];
        Arrays.fill(board, Cell.EMPTY);
        board[get1d(3, 3)] = Cell.WHITE;
        board[get1d(4, 4)] = Cell.WHITE;
        board[get1d(4, 3)] = Cell.BLACK;
        board[get1d(3, 4)] = Cell.BLACK;
        blackCells = 2;
        whiteCells = 2;
        emptyCells = 60;
    }

    public ArrayBoard(final GameBoard board) throws ServerException {
        blackCells = 0;
        whiteCells = 0;
        emptyCells = 64;
        this.board = new Cell[64];
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                this.board[get1d(x, y)] = board.getCell(x, y);
            }
        }
    }

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public Cell getCell(final int x, final int y) throws ServerException {
        return board[get1d(x, y)];
    }

    @Override
    public Cell getCell(final Point point) throws ServerException {
        return board[get1d(point.getX(), point.getY())];
    }

    @Override
    public void setCell(final int x, final int y, final Cell cell) {
        final Cell prev = board[get1d(x, y)];
        switch (prev) {
            case EMPTY:
                emptyCells--;
                break;
            case WHITE:
                whiteCells--;
                break;
            case BLACK:
                blackCells--;
                break;
        }
        switch (cell) {
            case EMPTY:
                emptyCells++;
                break;
            case WHITE:
                whiteCells++;
                break;
            case BLACK:
                blackCells++;
                break;
        }
        board[get1d(x, y)] = cell;
    }

    @Override
    public void setCell(final Point point, final Cell cell) {
        setCell(point.getX(), point.getY(), cell);
    }

    @Override
    public void reverseCell(final int x, final int y) throws ServerException {
        final int ind = get1d(x, y);
        final Cell cell = board[ind];
        switch (cell) {
            case EMPTY:
                throw new ServerException(GameErrorCode.INVALID_CELL);
            case WHITE:
                whiteCells--;
                board[ind] = Cell.BLACK;
                blackCells++;
                break;
            case BLACK:
                blackCells--;
                board[ind] = Cell.WHITE;
                whiteCells++;
                break;
        }
    }

    @Override
    public void reverseCell(final Point point) throws ServerException {
        reverseCell(point.getX(), point.getY());
    }

    @Override
    public void reverseCells(final Collection<Point> points) throws ServerException {
        for (final Point point : points) {
            reverseCell(point);
        }
    }

    @Override
    public int getCountBlackCells() {
        return blackCells;
    }

    @Override
    public int getCountWhiteCells() {
        return whiteCells;
    }

    @Override
    public int getCountEmpty() {
        return emptyCells;
    }

    @Override
    public boolean validate(final Point point) {
        return point.getX() >= 0 && point.getX() < 8
                && point.getY() >= 0 && point.getY() < 8;
    }

    @Override
    public GameBoard clone() {
        return null;
    }

    @Override
    public String toString() {
        return "null";
    }

    public static int get1d(final int x, final int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException(String.format("Illegal coordinates: (%d, %d) ", x, y));
        }
        return y * SIZE + x;
    }
}
