package selfplay;

import exception.ServerException;
import models.base.interfaces.GameBoard;
import models.board.Point;
import player.Player;
import base.Strategy;

public class BotPlayer extends Player {
    public BotPlayer(final String nickname, final Strategy strategy) {
        super(nickname, strategy);
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        return strategy.move(board);
    }

    public void setStrategy(final Strategy strategy) {
        this.strategy = strategy;
    }
}
