package logic;

import exception.ServerException;
import models.BaseAllBoards;
import models.base.interfaces.GameBoard;
import models.board.Point;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardUtilsTest extends BaseAllBoards {

    @ParameterizedTest
    @MethodSource("getAllBoards")
    void fromAndToStringArrayBoard(final GameBoard board) throws ServerException {
        final String s = BoardUtils.toString(board);
        final GameBoard fromString = BoardUtils.fromString(s);
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                final Point p = new Point(i, j);
                assertEquals(board.getCell(p), fromString.getCell(p));
            }
        }
        assertEquals(board.getCountBlackCells(), fromString.getCountBlackCells());
        assertEquals(board.getCountWhiteCells(), fromString.getCountWhiteCells());
        assertEquals(board.getCountEmpty(), fromString.getCountEmpty());
    }
}