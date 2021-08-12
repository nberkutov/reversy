package models.strategies;

import exception.ServerException;
import lombok.AllArgsConstructor;
import models.ai.minimax.MiniMaxInfo;
import models.ai.minimax.OneThreadMinimax;
import models.ai.minimax.tree.Tree;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.algorithms.SimpleAlgorithm;
import models.strategies.base.Algorithm;
import models.strategies.base.Strategy;

@AllArgsConstructor
public class MinimaxStrategy implements Strategy {
    private final int maxDeath;
    private final Algorithm algoPlayer;
    private final Algorithm algoBot;

    public MinimaxStrategy(int maxDeath, Algorithm algoPlayer) {
        this.maxDeath = maxDeath;
        this.algoPlayer = algoPlayer;
        algoBot = new SimpleAlgorithm();
    }


    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        final Tree branch = new Tree();
        final MiniMaxInfo info = new MiniMaxInfo(branch, maxDeath, color, algoPlayer, algoBot);
        final OneThreadMinimax oneThreadMinimax = new OneThreadMinimax(info);
        oneThreadMinimax.minimax(board, null, 0, color);
        return branch.getMove();
    }
}
