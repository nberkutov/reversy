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
    private final Strategy strategyPlayer;
    private final Strategy strategyBot;

    public MyBot(int depth, PlayerColor myColor, Strategy strategyPlayer, Strategy strategyBot) {
        this.depth = depth;
        this.myColor = myColor;
        this.strategyPlayer = strategyPlayer;
        this.strategyBot = strategyBot;
    }

    public int calculateMove(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws GameException {
        return 0;
    }

}
