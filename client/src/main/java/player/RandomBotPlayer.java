package player;

import exception.ServerException;
import models.base.interfaces.GameBoard;
import models.board.Point;
import strategy.RandomStrategy;

public class RandomBotPlayer extends Player {
    public RandomBotPlayer(final String nickname) {
        super(nickname, new RandomStrategy());
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        return strategy.move(board);
    }
}
