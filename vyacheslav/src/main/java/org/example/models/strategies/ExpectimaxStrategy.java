package org.example.models.strategies;


import org.example.exception.ServerException;
import org.example.models.ai.expectimax.Expectimax;
import org.example.models.ai.minimax.tree.Tree;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.algorithms.SimpleAlgorithm;
import org.example.models.strategies.base.Algorithm;
import org.example.models.strategies.base.Strategy;

public class ExpectimaxStrategy implements Strategy {
    private final int maxDeath;
    private final Algorithm algoPlayer;
    private final Algorithm algoBot;


    public ExpectimaxStrategy(int maxDeath, Algorithm algoPlayer) {
        this.maxDeath = maxDeath;
        this.algoPlayer = algoPlayer;
        algoBot = new SimpleAlgorithm();
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        Tree branch = new Tree();
        Expectimax expectimax = new Expectimax(maxDeath, color, algoPlayer, algoBot);
        expectimax.expectimax(branch, board, null, 0, color);
        return branch.getMove();
    }
}
