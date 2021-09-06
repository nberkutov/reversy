package org.example.models.strategies;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.exception.ServerException;
import org.example.models.ai.minimax.tree.Tree;
import org.example.models.ai.myai.MyBot;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Algorithm;
import org.example.models.strategies.base.Strategy;

@AllArgsConstructor
@Data
public class MyStrategy implements Strategy {
    private int maxDeath;
    private Algorithm algoPlayer;

    @Override
    public Point getMove(final GameBoard board, final PlayerColor color) throws ServerException {
        final Tree branch = new Tree();
        final MyBot myBot = new MyBot(maxDeath, color, algoPlayer);
        myBot.calculateMove(branch, board, null, 0, color);
        return branch.getMove();
    }

    public void incrementDeath() {
        maxDeath++;
        if (maxDeath >= 8) {
            maxDeath = 2;
        }
    }
}
