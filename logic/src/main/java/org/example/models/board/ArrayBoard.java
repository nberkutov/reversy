package org.example.models.board;

import lombok.Data;
import lombok.SneakyThrows;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.logic.BoardUtils;
import org.example.models.GameProperties;
import org.example.models.base.Cell;
import org.example.models.base.interfaces.GameBoard;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Data
@Entity(name = "board")
public class ArrayBoard implements GameBoard {
    @Column
    private final int size;
    @Transient
    private final Cell[] board;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String textCells;
    @Column
    private int whiteCells;
    @Column
    private int blackCells;
    @Column
    private int emptyCells;

    public ArrayBoard() {
        size = GameProperties.BOARD_SIZE;
        board = new Cell[size * size];
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
        size = board.getSize();
        blackCells = 0;
        whiteCells = 0;
        emptyCells = 0;
        this.board = new Cell[size * size];
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                final Cell cell = board.getCell(x, y);
                this.board[get1d(x, y)] = cell;
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
            }
        }
    }


    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Cell getCell(final int x, final int y) {
        return board[get1d(x, y)];
    }

    @Override
    public Cell getCell(final Point point) {
        return board[get1d(point.getX(), point.getY())];
    }

    @Override
    public void setCell(final int x, final int y, final Cell cell) {
        final int index = get1d(x, y);
        final Cell prev = board[index];
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
        board[index] = cell;
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
        return point != null &&
                point.getX() >= 0 && point.getX() < size
                && point.getY() >= 0 && point.getY() < size;
    }

    @SneakyThrows
    @Override
    public GameBoard clone() {
        return new ArrayBoard(this);
    }

    @Override
    public String toString() {
        return "ArrayBoard{" +
                "size=" + size +
                ", board=" + Arrays.toString(board) +
                ", whiteCells=" + whiteCells +
                ", blackCells=" + blackCells +
                ", emptyCells=" + emptyCells +
                '}';
    }

    @Override
    public void updateTextCells() throws ServerException {
        textCells = BoardUtils.toString(this);
    }

    @Override
    public void updateCellsByText() {
        blackCells = 2;
        whiteCells = 2;
        emptyCells = 60;
        BoardUtils.updateCellsByTextCells(this);
    }

    private int get1d(final int x, final int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException(String.format("Illegal coordinates: (%d, %d) ", x, y));
        }
        return y * size + x;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayBoard)) return false;
        final ArrayBoard that = (ArrayBoard) o;
        return getId() == that.getId() && getSize() == that.getSize() && getWhiteCells() == that.getWhiteCells() && getBlackCells() == that.getBlackCells() && getEmptyCells() == that.getEmptyCells() && Arrays.equals(getBoard(), that.getBoard());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getSize(), getWhiteCells(), getBlackCells(), getEmptyCells());
        result = 31 * result + Arrays.hashCode(getBoard());
        return result;
    }
}
