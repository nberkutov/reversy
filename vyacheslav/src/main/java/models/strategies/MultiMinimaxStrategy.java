package models.strategies;

import exception.ServerException;
import lombok.AllArgsConstructor;
import models.ai.minimax.MiniMaxInfo;
import models.ai.minimax.MultiThreadMinimax;
import models.ai.minimax.tree.Tree;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.algorithms.SimpleAlgorithm;
import models.strategies.base.Algorithm;
import models.strategies.base.Strategy;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@AllArgsConstructor
public class MultiMinimaxStrategy implements Strategy {
    private final int maxDeath;
    private final Algorithm algoPlayer;
    private final Algorithm algoBot;

    public MultiMinimaxStrategy(int maxDeath, Algorithm algoPlayer) {
        this.maxDeath = maxDeath;
        this.algoPlayer = algoPlayer;
        algoBot = new SimpleAlgorithm();
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        final ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfProcessors);
        Tree branch = new Tree();
        MiniMaxInfo info = new MiniMaxInfo(branch, maxDeath, color, algoPlayer, algoBot);
        MultiThreadMinimax minimax = new MultiThreadMinimax(board, color, null, 0, info);
        final ForkJoinTask<Integer> result = forkJoinPool.submit(minimax);
        result.join();
        return branch.getMove();
    }
}
