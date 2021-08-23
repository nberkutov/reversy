package org.example.models.strategies;

import lombok.AllArgsConstructor;
import org.example.exception.ServerException;
import org.example.models.ai.minimax.MiniMaxInfo;
import org.example.models.ai.minimax.MultiThreadMinimax;
import org.example.models.ai.minimax.tree.Tree;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.algorithms.SimpleAlgorithm;
import org.example.models.strategies.base.Algorithm;
import org.example.models.strategies.base.Strategy;

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
