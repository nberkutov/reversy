package client.models.ai.myai;

import client.models.ai.minimax.tree.Tree;
import client.models.strategies.Strategy;
import exception.GameException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

public class MyBot {
    private final int depth;
    private final PlayerColor myColor;
    private final Strategy strategy;


    public MyBot(int depth, PlayerColor myColor, Strategy strategy) {
        this.depth = depth;
        this.myColor = myColor;
        this.strategy = strategy;
    }

    public int calculateMove(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws GameException {
        return 0;
    }

}
