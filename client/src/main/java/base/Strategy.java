package base;

import logic.BoardLogic;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import exception.ServerException;

import java.util.List;

public interface Strategy {
    /**
     * Выбирает для игрока ход на доске.
     * @param board текущая игровая доска.
     * @return ход.
     */
    Point move(GameBoard board) throws ServerException;

    /**
     * Устанавливает цвет игрока, для которого выбирается ход.
     */
    void setColor(PlayerColor color);
}
