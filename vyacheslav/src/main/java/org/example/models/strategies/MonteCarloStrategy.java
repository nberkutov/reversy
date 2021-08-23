package org.example.models.strategies;


import lombok.AllArgsConstructor;
import org.example.exception.ServerException;
import org.example.models.ai.montecarlo.MonteCarloTreeSearch;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.base.Strategy;

@AllArgsConstructor
public class MonteCarloStrategy implements Strategy {
    private final int time;

    public MonteCarloStrategy() {
        this(3);
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        MonteCarloTreeSearch monte = new MonteCarloTreeSearch(time);
        return monte.findNextMove(board, color);
    }
}
