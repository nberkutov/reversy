package client.models.ai.myai;

import client.models.ai.minimax.tree.Tree;
import client.models.strategies.Strategy;
import exception.GameException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

public class MyBot {

    public MyBot(int maxDeath, PlayerColor color, Strategy strategyPlayer) {
    }

    public int calculateMove(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws GameException {
        return 0;
    }

}
