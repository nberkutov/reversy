package models.strategies;


import exception.ServerException;
import models.ai.expectimax.Expectimax;
import models.ai.minimax.tree.Tree;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.algorithms.SimpleAlgorithm;
import models.strategies.base.Algorithm;
import models.strategies.base.Strategy;

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
