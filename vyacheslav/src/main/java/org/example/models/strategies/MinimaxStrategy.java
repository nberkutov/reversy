package org.example.models.strategies;

import lombok.AllArgsConstructor;
import org.example.exception.ServerException;
import org.example.models.ai.minimax.MiniMaxInfo;
import org.example.models.ai.minimax.OneThreadMinimax;
import org.example.models.ai.minimax.tree.Tree;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.algorithms.SimpleAlgorithm;
import org.example.models.strategies.base.Algorithm;
import org.example.models.strategies.base.Strategy;

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
